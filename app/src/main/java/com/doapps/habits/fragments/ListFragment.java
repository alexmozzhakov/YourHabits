package com.doapps.habits.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.adapter.HabitRecycleAdapter;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.SimpleItemTouchHelperCallback;
import com.doapps.habits.models.IHabitDatabaseMovableListProvider;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.habits_list);
        recyclerView.setHasFixedSize(true);
        TextView emptyView = (TextView) result.findViewById(R.id.empty_view);
        IHabitDatabaseMovableListProvider habitListManager = HabitListManager.getInstance(getContext());

        if (habitListManager.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(llm);
            HabitRecycleAdapter recycleAdapter =
                    new HabitRecycleAdapter(habitListManager);
            recyclerView.setAdapter(recycleAdapter);
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(recycleAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }

        return result;
    }
}
