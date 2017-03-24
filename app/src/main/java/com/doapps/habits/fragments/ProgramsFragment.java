package com.doapps.habits.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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

import com.bumptech.glide.Glide;
import com.doapps.habits.R;
import com.doapps.habits.adapter.ProgramRecycleAdapter;
import com.doapps.habits.models.FirebaseProgramView;
import com.doapps.habits.models.IProgramViewProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProgramsFragment extends Fragment {
    /**
     * TAG is defined for logging errors and debugging information
     */
    private static final String TAG = ProgramsFragment.class.getSimpleName();
    public static boolean isShowing;
    private static boolean persistenceEnabled;
    private static boolean isTop;
    private ProgramRecycleAdapter mAdapter;
    private List<IProgramViewProvider> mProgramData;
    private TextView mTitleTop;
    private TextView mSuccessTop;
    private Typeface mFaceLight;
    private Typeface mFace;
    private ImageView mImageTop;
    private boolean isEmpty;
    private DatabaseReference mRootRef;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AssetManager assets = context.getAssets();
        if (assets != null) {
            mFaceLight = Typeface.createFromAsset(assets, "Montserrat-Light.otf");
            mFace = Typeface.createFromAsset(assets, "Montserrat-Regular.ttf");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_programs, container, false);

        if (!persistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistenceEnabled = true;
        }

        mRootRef = FirebaseDatabase.getInstance().getReference().child("programs");
        View emptyView = result.findViewById(R.id.empty_view);
        RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.recyclerView);
        mImageTop = (ImageView) result.findViewById(R.id.imageViewTop);

        ConnectivityManager conMan = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            sharedPreferences.edit().putBoolean("downloaded", true).apply();
            Log.i("FD", "User downloaded database");
        }
        if (sharedPreferences.getBoolean("downloaded", false)) {
            emptyView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            mImageTop.setVisibility(View.VISIBLE);
            Log.i("FD", "Database is already downloaded");
        } else {
            isEmpty = true;
        }

        mProgramData = new ArrayList<>(5);
        mAdapter = new ProgramRecycleAdapter(mProgramData, getActivity(),
                getChildFragmentManager(), this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(mAdapter);

        mSuccessTop = (TextView) result.findViewById(R.id.percentTop);
        mImageTop.setMaxHeight(result.getHeight() / 3);
        mTitleTop = (TextView) result.findViewById(R.id.titleTop);


        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int position = Integer.parseInt(dataSnapshot.getKey());

                Log.i(TAG, "onChildAdded:()");
                if (position == 0) {
                    if (isEmpty) {
                        emptyView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mImageTop.setVisibility(View.VISIBLE);
                    }
                    generateTopProgram();
                } else {
                    mProgramData.add(position - 1, new FirebaseProgramView(dataSnapshot));
                }

                mAdapter.notifyItemInserted(position - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildChanged: ()");
                int position = Integer.parseInt(dataSnapshot.getKey()) - 1;

                if (position >= 0) {
                    mProgramData.remove(position);
                    mProgramData.add(position, new FirebaseProgramView(dataSnapshot));
                }

                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved()");
                int position = Integer.parseInt(dataSnapshot.getKey()) - 1;
                if (position >= 0) {
                    mProgramData.remove(position);
                }
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // mAdapter.notifyItemMoved();
                Log.i(TAG, "onChildMoved: ()");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });

        return result;
    }

    private void generateTopProgram() {
        DatabaseReference programRef = mRootRef.child("0");

        programRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTitleTop.setText(dataSnapshot.child("name").getValue(String.class));
                String imageLink = String.format("http://habbitsapp.esy.es/img_progs/%s.jpg",
                        dataSnapshot.child("image").getValue(String.class));
                Log.v("IMG_URL", imageLink);

                if (getActivity() != null) {
                    Glide.with(getActivity().getApplicationContext())
                            .load(imageLink)
                            .into(mImageTop);
                }

                mTitleTop.setTypeface(mFaceLight);

                if (isShowing) {
                    createProgramApplyFragment(dataSnapshot, getChildFragmentManager());
                }

                mImageTop.setOnClickListener(view -> {
                    onClick(dataSnapshot, getChildFragmentManager());
                    isTop = true;
                });
                mTitleTop.setOnClickListener(view -> {
                    onClick(dataSnapshot, getChildFragmentManager());
                    isTop = true;
                });

                //Style percent view
                mSuccessTop.setVisibility(View.VISIBLE);
                mSuccessTop.setText(dataSnapshot.child("success").getValue(String.class));
                mSuccessTop.setTypeface(mFace);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.toString());
            }
        });
    }

    public void onClick(DataSnapshot dataSnapshot, FragmentManager fragmentManager) {
        if (isShowing) {
            if (!isTop) {
                generateTopProgram();
            }

            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentById(R.id.recyclerLayout))
                    .commit();
            isShowing = false;
        } else {
            createProgramApplyFragment(dataSnapshot, fragmentManager);
            isTop = false;
        }
    }

    private static void createProgramApplyFragment(DataSnapshot dataSnapshot,
                                                   FragmentManager fragmentManager) {

        // delete previous fragment if showing
        if (isShowing) {
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentById(R.id.recyclerLayout))
                    .commit();
        }

        ProgramFragment programFragment = new ProgramFragment();
        programFragment.setSnapshot(dataSnapshot);

        fragmentManager.beginTransaction()
                .replace(R.id.recyclerLayout, programFragment)
                .commit();

        isShowing = true;
    }

}
