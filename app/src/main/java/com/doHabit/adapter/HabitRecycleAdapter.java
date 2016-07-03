package com.dohabit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dohabit.R;
import com.dohabit.database.HabitDBHandler;
import com.dohabit.models.Habit;
import com.dohabit.models.HabitsDatabase;
import com.dohabit.models.MovableItem;
import com.dohabit.viewholder.HabitViewHolder;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HabitRecycleAdapter extends RecyclerView.Adapter implements MovableItem {

    private final List<Habit> mHabitList;
    private final HabitsDatabase mHabitsDatabase;

    public HabitRecycleAdapter(final List<Habit> data, final Context context) {
        mHabitsDatabase = HabitDBHandler.getHabitDatabase(context);
        mHabitList = data;
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
        habitViewHolder.checkBox.setOnClickListener(v -> {
            if (habitViewHolder.checkBox.isChecked()) {
                mHabitList.get(position).setDoneMarker(true);

                mHabitsDatabase.updateHabit(
                        position,
                        habit.markerUpdatedDay,
                        habit.markerUpdatedMonth,
                        habit.markerUpdatedYear,
                        1);

                habitViewHolder.txtTitle.setTextColor(Color.GRAY);
            } else {

                mHabitsDatabase.updateHabit(
                        position,
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
        mHabitsDatabase.delete(mHabitList.get(position).id);
        mHabitList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mHabitList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mHabitList, i, i - 1);
            }
        }
        mHabitsDatabase.move(mHabitList.get(fromPosition).id, mHabitList.get(toPosition).id);
        notifyItemMoved(fromPosition, toPosition);
    }

}