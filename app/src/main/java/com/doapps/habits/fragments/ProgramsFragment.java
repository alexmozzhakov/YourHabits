package com.doapps.habits.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.adapter.ProgramRecycleAdapter;
import com.doapps.habits.helper.FirebaseDatabaseManager;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.models.Achievement;
import com.doapps.habits.models.FirebaseProgramView;
import com.doapps.habits.models.HabitsDatabase;
import com.doapps.habits.models.Program;
import com.doapps.habits.models.ProgramViewProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProgramsFragment extends Fragment {
    private static final String TAG = ProgramsFragment.class.getSimpleName();
    public static boolean isShowing;
    private ProgramRecycleAdapter mAdapter;
    private List<ProgramViewProvider> mProgramData;
    private TextView mTitleTop;
    private TextView mSuccessTop;
    private Typeface mFaceLight;
    private Typeface mFace;
    private ImageView mImageTop;
    private boolean isEmpty;
    private DatabaseReference mRootRef;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        final AssetManager assets = context.getAssets();
        if (assets != null) {
            mFaceLight = Typeface.createFromAsset(assets, "Montserrat-Light.otf");
            mFace = Typeface.createFromAsset(assets, "Montserrat-Regular.ttf");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_programs, container, false);
        mRootRef = FirebaseDatabaseManager.getInstance().getDatabase().child("programs");
        final View emptyView = result.findViewById(R.id.empty_view);
        final RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.recyclerView);

        final ConnectivityManager conMan = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        final NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            sharedPreferences.edit().putBoolean("downloaded", true).apply();
            Log.i("FD", "User downloaded database");
        }
        if (sharedPreferences.getBoolean("downloaded", false)) {
            emptyView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.i("FD", "Database is already downloaded");
        } else {
            isEmpty = true;
        }

        mProgramData = new ArrayList<>(5);
        mAdapter = new ProgramRecycleAdapter(mProgramData, getActivity(),
                getChildFragmentManager(), this);

        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);

        mSuccessTop = (TextView) result.findViewById(R.id.percentTop);
        mImageTop = (ImageView) result.findViewById(R.id.imageViewTop);
        mTitleTop = (TextView) result.findViewById(R.id.titleTop);


        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, final String s) {
                final int position = Integer.parseInt(dataSnapshot.getKey());

                Log.i(TAG, "onChildAdded:()");
                if (position == 0) {
                    if (isEmpty) {
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    generateTopProgram();
                } else {
                    mProgramData.add(position - 1, new FirebaseProgramView(dataSnapshot));
                }

                mAdapter.notifyItemInserted(position - 1);
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, final String s) {
                Log.i(TAG, "onChildChanged: ()");
                final int position = Integer.parseInt(dataSnapshot.getKey()) - 1;

                if (position >= 0) {
                    mProgramData.remove(position);
                    mProgramData.add(position, new FirebaseProgramView(dataSnapshot));
                }

                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved()");
                final int position = Integer.parseInt(dataSnapshot.getKey()) - 1;
                if (position >= 0) {
                    mProgramData.remove(position);
                }
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(final DataSnapshot dataSnapshot, final String s) {
                // mAdapter.notifyItemMoved();
                Log.i(TAG, "onChildMoved: ()");
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });

        return result;
    }

    static Program onProgramApply(final DataSnapshot dataSnapshot, final Context context) {
        final HabitsDatabase habitsDatabase = new HabitListManager(context).getDatabase();
        final long habitId = habitsDatabase.addHabit(
                dataSnapshot.child("habit").child("title").getValue(String.class),
                dataSnapshot.child("habit").child("question").getValue(String.class),
                dataSnapshot.child("habit").child("time").getValue(Integer.class),
                Calendar.getInstance(),
                0,
                dataSnapshot.child("habit").child("cost").getValue(Integer.class),
                dataSnapshot.child("habit").child("frequency").getValue(Integer.class)
        );

        final List<Achievement> achievements =
                createAchievementList(dataSnapshot.child("achievements"));

        return new Program(
                Integer.valueOf(dataSnapshot.getKey()),
                dataSnapshot.child("name").getValue(String.class),
                String.format("%s SUCCESS", dataSnapshot.child("success").getValue(String.class)),
                habitId,
                achievements
        );
    }

    private static List<Achievement> createAchievementList(final DataSnapshot dataSnapshot) {
        final List<Achievement> achievements = new ArrayList<>((int) dataSnapshot.getChildrenCount());
        for (final DataSnapshot achievementSnapshot : dataSnapshot.getChildren()) {
            final List<String> templates =
                    new ArrayList<>((int) achievementSnapshot.getChildrenCount());
            for (final DataSnapshot templatesSnapshot : achievementSnapshot.child("templates").getChildren()) {
                templates.add(templatesSnapshot.child("name").getValue(String.class));
            }
            final Achievement achievement = new Achievement(
                    achievementSnapshot.child("rating").getValue(Integer.class),
                    templates);
            achievements.add(achievement);
        }
        return achievements;
    }

    private void generateTopProgram() {
        final DatabaseReference programRef = mRootRef.child("0");

        programRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mTitleTop.setText(dataSnapshot.child("name").getValue(String.class));
                mTitleTop.setTextColor(Color.WHITE);
                final String imageLink = String.format("http://habbitsapp.esy.es/img_progs/%s.jpg",
                        dataSnapshot.child("image").getValue(String.class));
                Log.v("IMG_URL", imageLink);

                if (getActivity() != null) {
                    Picasso.with(getActivity().getApplicationContext())
                            .load(imageLink)
                            .into(mImageTop);
                }

                mTitleTop.setTypeface(mFaceLight);

                if (isShowing) {
                    createProgramApplyFragment(dataSnapshot, getChildFragmentManager());
                }

                mImageTop.setOnClickListener(view ->
                        onClick(dataSnapshot, getChildFragmentManager()));
                mTitleTop.setOnClickListener(view ->
                        onClick(dataSnapshot, getChildFragmentManager()));

                //Style percent view
                mSuccessTop.setVisibility(View.VISIBLE);
                mSuccessTop.setText(dataSnapshot.child("success").getValue(String.class));
                mSuccessTop.setTypeface(mFace);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.toString());
            }
        });
    }

    public void onClick(final DataSnapshot snapshot, final FragmentManager fm) {
        if (isShowing) {
            generateTopProgram();

            fm.beginTransaction()
                    .remove(fm.findFragmentById(R.id.recyclerLayout))
                    .commit();
            isShowing = false;
        } else {
            createProgramApplyFragment(snapshot, fm);
        }
    }

    private static void createProgramApplyFragment(final DataSnapshot snapshot,
                                                   final FragmentManager fm) {

        // delete previous fragment if showing
        if (isShowing) {
            fm.beginTransaction()
                    .remove(fm.findFragmentById(R.id.recyclerLayout))
                    .commit();
        }

        final ProgramFragment programFragment = new ProgramFragment();
        programFragment.setSnapshot(snapshot);

        fm.beginTransaction()
                .replace(R.id.recyclerLayout, programFragment)
                .commit();

        isShowing = true;
    }

}
