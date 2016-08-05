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
import com.doapps.habits.models.HabitDatabaseMovableListProvider;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        final TextView emptyView = (TextView) result.findViewById(R.id.empty_view);
        final HabitDatabaseMovableListProvider habitListManager = HabitListManager.getInstance(getContext());

        if (habitListManager.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(llm);
            final HabitRecycleAdapter recycleAdapter =
                    new HabitRecycleAdapter(habitListManager);
            recyclerView.setAdapter(recycleAdapter);
            final ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(recycleAdapter);
            final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }

        return result;
    }
}
