package com.doapps.habits.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.listeners.EmptyListListener;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitDatabaseMovableListProvider;
import com.doapps.habits.models.IHabitsDatabase;
import com.doapps.habits.models.IMovableList;
import com.doapps.habits.viewholder.HabitViewHolder;

import java.util.Calendar;
import java.util.List;

public class HabitRecycleAdapter extends RecyclerView.Adapter<HabitViewHolder> implements IMovableList {
    private final List<Habit> mHabitList;
    private final IHabitsDatabase mHabitsDatabase;
    private final IHabitDatabaseMovableListProvider mMovableList;

    public HabitRecycleAdapter(IHabitDatabaseMovableListProvider movableList) {
        mMovableList = movableList;
        mHabitList = movableList.getList();
        mHabitsDatabase = movableList.getDatabase();
    }

    @Override
    public HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {
        Log.d("HabitAdapter", "Position is: " + position);
        Habit habit = mHabitList.get(position);

        holder.txtTitle.setText(habit.title);
        holder.checkBox.setOnClickListener(view -> {
            if (holder.checkBox.isChecked()) {
                mHabitsDatabase.updateHabit(habit, 1);
                mHabitList.get(position).setDoneMarker(true);
                holder.txtTitle.setTextColor(Color.GRAY);
            } else {
                mHabitsDatabase.updateHabit(habit, 0);
                mHabitList.get(position).setDoneMarker(false);
                holder.txtTitle.setTextColor(Color.BLACK);
            }
        });

        Calendar calendar = Calendar.getInstance();

        holder.checkBox.setChecked(habit.isDone(
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)));

    }

    @Override
    public int getItemCount() {
        return mHabitList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        mMovableList.onItemDismiss(position);
        notifyItemRemoved(position);
        EmptyListListener.listener.isEmpty(getItemCount() == 0);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mMovableList.onItemMove(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
}