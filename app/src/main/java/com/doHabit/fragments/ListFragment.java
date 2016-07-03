package com.dohabit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dohabit.R;
import com.dohabit.adapter.HabitRecycleAdapter;
import com.dohabit.helper.HabitListManager;
import com.dohabit.helper.SimpleItemTouchHelperCallback;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_list, container, false);

        final RecyclerView recyclerView = (RecyclerView) result.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        final TextView emptyView = (TextView) result.findViewById(R.id.empty_view);

        if (HabitListManager.getInstance(getContext()).isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(llm);
            final HabitRecycleAdapter recycleAdapter =
                    new HabitRecycleAdapter(HabitListManager.getHabitsList(),
                            getActivity());
            recyclerView.setAdapter(recycleAdapter);
            final ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(recycleAdapter);
            final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
        }

        return result;
    }
}
