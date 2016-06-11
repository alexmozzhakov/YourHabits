package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.adapter.HabitRecycleAdapter;
import com.habit_track.database.HabitDBHandler;
import com.habit_track.helper.SimpleItemTouchHelperCallback;
import com.habit_track.models.Habit;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements SearchView.OnQueryTextListener {
    static List<Habit> mHabitsList;

    private static HabitRecycleAdapter mRecycleAdapter;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);
        HabitDBHandler mHabitsDatabase = HabitDBHandler.getInstance(getActivity().getApplicationContext());

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

        mRecycleAdapter = new HabitRecycleAdapter(mHabitsList, getActivity());
        mRecyclerView.setAdapter(mRecycleAdapter);

        final ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mRecycleAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        mRecycleAdapter.setFilter(mHabitsList);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Habit> filteredModelList = filter(mHabitsList, newText);
        mRecycleAdapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Habit> filter(List<Habit> habits, String query) {
        query = query.toLowerCase();

        final List<Habit> filteredModelList = new ArrayList<>();
        for (Habit habit : habits) {
            final String text = habit.title.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(habit);
            }
        }
        return filteredModelList;
    }
}
