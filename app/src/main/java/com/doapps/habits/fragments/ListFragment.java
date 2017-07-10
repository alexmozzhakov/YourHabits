package com.doapps.habits.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.adapter.HabitRecycleAdapter;
import com.doapps.habits.adapter.HabitSearchAdapter;
import com.doapps.habits.adapter.IMovableListAdapter;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.SimpleItemTouchHelperCallback;
import com.doapps.habits.listeners.EmptyListListener;
import com.doapps.habits.models.Habit;
import com.doapps.habits.view.holders.HabitViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListFragment extends Fragment implements SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

  private SearchView searchView;
  private RecyclerView.Adapter<HabitViewHolder> recycleAdapter;
  private RecyclerView recyclerView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_list, container, false);
    recyclerView = result.findViewById(R.id.habits_list);
    recyclerView.setHasFixedSize(true);

    TextView emptyView = result.findViewById(R.id.empty_view);
    Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
    toolbar.setTitle(R.string.lists);
    EmptyListListener.Companion.getListener().addObserver((o, arg) -> {
      recyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
      if (searchView != null) {
        searchView.setVisibility(View.GONE);
      }
    });

    HabitListManager habitListManager = HabitListManager.getInstance(getContext());
    try {
      if (habitListManager.getList().isEmpty()) {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
      } else {
        try {
          Log.i("List", String.valueOf(Arrays.toString(habitListManager.getList().toArray())));
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recycleAdapter = new HabitRecycleAdapter(habitListManager);
        recyclerView.setAdapter(recycleAdapter);
        Callback callback = new SimpleItemTouchHelperCallback((IMovableListAdapter) recycleAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        searchView = toolbar.getRootView().findViewById(R.id.toolbar_search);
        searchView.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public boolean onQueryTextSubmit(String s) {
    s = s.toLowerCase();
    List<Habit> habitList;
    try {
      habitList = HabitListManager.getInstance(getContext()).getList();
      List<Habit> searchResult = new ArrayList<>(habitList.size());
      for (int i = 0; i < habitList.size(); i++) {
        if (habitList.get(i).getTitle().toLowerCase().contains(s)) {
          searchResult.add(habitList.get(i));
        }
      }
      recycleAdapter = new HabitSearchAdapter(searchResult,
          HabitListManager.getInstance(getContext()).getDatabase().habitDao());
      recyclerView.setAdapter(recycleAdapter);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean onQueryTextChange(String s) {
    return false;
  }

  @Override
  public void onDestroy() {
    if (searchView != null) {
      searchView.setVisibility(View.GONE);
    }
    super.onDestroy();
  }

  @Override
  public boolean onClose() {
    recycleAdapter = new HabitRecycleAdapter(HabitListManager.getInstance(getContext()));
    recyclerView.setAdapter(recycleAdapter);
    return false;
  }
}
