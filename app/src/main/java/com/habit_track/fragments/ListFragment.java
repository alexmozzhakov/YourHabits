package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.app.Habit;
import com.habit_track.helper.HabitDBHandler;
import com.habit_track.helper.RecycleHabitAdapter;
import com.habit_track.helper.SimpleItemTouchHelperCallback;

import java.util.List;

public class ListFragment extends Fragment {
    public static HabitDBHandler mHabitsDatabase;
    public static List<Habit> mHabitsList;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);

        if (mHabitsDatabase == null) {
            mHabitsDatabase = new HabitDBHandler(this.getActivity());
        }

        if (mHabitsDatabase.notSame() || mHabitsList == null) {
            mHabitsList = mHabitsDatabase.getHabitDetailsAsArrayList();
        }

        final RecyclerView mRecyclerView = (RecyclerView) result.findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);

        final RecycleHabitAdapter mRecycleAdapter = new RecycleHabitAdapter(mHabitsList);
        mRecyclerView.setAdapter(mRecycleAdapter);

        final ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mRecycleAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        return result;
    }

}
