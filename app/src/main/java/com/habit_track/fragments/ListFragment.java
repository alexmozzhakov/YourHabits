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

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);

        if (mHabitsDatabase == null) {
            mHabitsDatabase = new HabitDBHandler(this.getActivity());
        }

        // if (habitList == null)
        final List<Habit> habitList = mHabitsDatabase.getHabitDetailsAsArrayList();

        final RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        final RecycleHabitAdapter adapter = new RecycleHabitAdapter(habitList);
        recyclerView.setAdapter(adapter);

        final ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        return result;
    }

}
