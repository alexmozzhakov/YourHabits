package com.habit_track.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.habit_track.R;
import com.habit_track.app.Habit;
import com.habit_track.helper.HabitDBHandler;
import com.habit_track.helper.HabitListAdapter;

import java.util.ArrayList;

public class ListFragment extends android.app.ListFragment {
    public static HabitDBHandler habitsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_list, container, false);

        habitsDatabase = new HabitDBHandler(this.getActivity());

        //if (habitList == null)
        final ArrayList<Habit> habitList = habitsDatabase.getHabitDetailsAsArrayList();

        final HabitListAdapter adapter = new HabitListAdapter(getActivity(),
                R.layout.habit_listitem, habitList);
        final ListView listView = (ListView) result.findViewById(android.R.id.list);

        listView.setAdapter(adapter);


        return result;
    }

}
