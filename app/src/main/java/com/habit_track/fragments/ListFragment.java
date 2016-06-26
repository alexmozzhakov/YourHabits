package com.habit_track.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.adapter.HabitRecycleAdapter;
import com.habit_track.helper.HabitListManager;
import com.habit_track.helper.SimpleItemTouchHelperCallback;

public class ListFragment extends Fragment {

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);

        final RecyclerView mRecyclerView = (RecyclerView) result.findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);

        final HabitRecycleAdapter mRecycleAdapter =
                new HabitRecycleAdapter(HabitListManager.getInstance(getContext()).getHabitsList(),
                        getActivity());
        mRecyclerView.setAdapter(mRecycleAdapter);

        final ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mRecycleAdapter);

        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        return result;
    }
}
