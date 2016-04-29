package com.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.habit_track.R;
import com.habit_track.activity.MainActivity;
import com.habit_track.app.AppController;
import com.habit_track.app.Program;
import com.habit_track.helper.ProgramListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProgramsFragment extends Fragment {
    private static boolean isShowing;
    private BaseAdapter mAdapter;
    private List<Program> mProgramData;
    private TextView mTitleTop;
    private Firebase mFirebaseRef;
    private TextView mSuccessTop;
    private Typeface faceLight;
    private Typeface face;
    private ImageView mImageTop;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        face = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.ttf");
        faceLight = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Light.otf");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_programs, container, false);

        mProgramData = new ArrayList<>();
        mFirebaseRef = new Firebase(AppController.URL_PROGRAMS_API);
        mAdapter = new ProgramListAdapter(
                getActivity(), R.layout.program_list_item, mProgramData);
        final ListView listView = (ListView) result.findViewById(R.id.mainListView);
        listView.setAdapter(mAdapter);

        new Thread() {
            @Override
            public void run() {
                mSuccessTop = (TextView) result.findViewById(R.id.percentTop);
                mImageTop = (ImageView) result.findViewById(R.id.imageViewTop);
                mTitleTop = (TextView) result.findViewById(R.id.titleTop);
            }
        }.start();

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseRef.child("programs").child("other").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mProgramData.add(Integer.parseInt(dataSnapshot.getKey()) - 1,
                        new Program(dataSnapshot.child("name").getValue(String.class),
                                dataSnapshot.child("success").getValue(String.class) + " SUCCESS"));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int position = Integer.parseInt(dataSnapshot.getKey()) - 1;
                mProgramData.remove(position);
                mProgramData.add(position, new Program(dataSnapshot.child("name").getValue(String.class),
                        dataSnapshot.child("success").getValue(String.class) + " SUCCESS"));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = Integer.parseInt(dataSnapshot.getKey()) - 1;
                mProgramData.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseError", firebaseError.toString());
            }
        });

        mFirebaseRef.child("programs").child("top").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mTitleTop.setText(snapshot.child("name").getValue(String.class));
                mTitleTop.setTextColor(Color.WHITE);
                final String imageLink = "http://habbitsapp.esy.es/img_progs/"
                        + snapshot.child("image").getValue(String.class) + ".jpg";
                Log.v("IMG_URL", imageLink);
                Picasso.with(getActivity().getApplicationContext())
                        .load(imageLink)
                        .into(mImageTop);
                mTitleTop.setTypeface(faceLight);
                mTitleTop.setOnClickListener(this::top);

                //Style percent view
                mSuccessTop.setText(snapshot.child("success").getValue(String.class));
                mSuccessTop.setTypeface(face);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                Log.e("FirebaseError", error.toString());
            }

            public void top(final View view) {
                if (!isShowing) {
                    isShowing = true;
                } else {
                    getFragmentManager().beginTransaction().remove(MainActivity.lastFragment).commit();
                    isShowing = false;
                }
            }
        });
    }
}