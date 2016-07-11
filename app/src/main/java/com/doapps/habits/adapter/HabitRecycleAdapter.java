package com.doapps.habits.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.HabitDatabaseMovableListProvider;
import com.doapps.habits.models.HabitsDatabase;
import com.doapps.habits.models.MovableList;
import com.doapps.habits.viewholder.HabitViewHolder;

import java.util.Calendar;
import java.util.List;

public class HabitRecycleAdapter extends RecyclerView.Adapter implements MovableList {

    private final List<Habit> mHabitList;
    private final HabitsDatabase mHabitsDatabase;
    private final HabitDatabaseMovableListProvider movableList;

    public HabitRecycleAdapter(final HabitDatabaseMovableListProvider movableList) {
        this.movableList = movableList;
        mHabitList = movableList.getList();
        mHabitsDatabase = movableList.getDatabase();
    }

    @Override
    public HabitViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final HabitViewHolder habitViewHolder = (HabitViewHolder) holder;
        final Habit habit = mHabitList.get(position);
        habitViewHolder.txtTitle.setText(habit.title);
        habitViewHolder.checkBox.setOnClickListener(view -> {
            if (habitViewHolder.checkBox.isChecked()) {
                mHabitList.get(position).setDoneMarker(true);

                mHabitsDatabase.updateHabit(
                        habit.id,
                        habit.markerUpdatedDay,
                        habit.markerUpdatedMonth,
                        habit.markerUpdatedYear,
                        1);

                habitViewHolder.txtTitle.setTextColor(Color.GRAY);
            } else {

                mHabitsDatabase.updateHabit(
                        habit.id,
                        habit.markerUpdatedDay,
                        habit.markerUpdatedMonth,
                        habit.markerUpdatedYear,
                        0);

                habitViewHolder.txtTitle.setTextColor(Color.BLACK);
                mHabitList.get(position).setDoneMarker(false);
            }
        });

        final Calendar calendar = Calendar.getInstance();

        habitViewHolder.checkBox.setChecked(habit.isDone(
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)));

    }

    @Override
    public int getItemCount() {
        return mHabitList.size();
    }

    @Override
    public void onItemDismiss(final int position) {
        movableList.onItemDismiss(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        movableList.onItemMove(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

}