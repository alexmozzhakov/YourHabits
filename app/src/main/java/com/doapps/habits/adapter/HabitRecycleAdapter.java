package com.doapps.habits.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitDatabaseMovableListProvider;
import com.doapps.habits.models.IHabitsDatabase;
import com.doapps.habits.models.IMovableList;
import com.doapps.habits.viewholder.HabitViewHolder;

import java.util.Calendar;
import java.util.List;

public class HabitRecycleAdapter extends RecyclerView.Adapter implements IMovableList {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        HabitViewHolder habitViewHolder = (HabitViewHolder) holder;
        Habit habit = mHabitList.get(position);

        habitViewHolder.txtTitle.setText(habit.title);
        habitViewHolder.checkBox.setOnClickListener(view -> {
            if (habitViewHolder.checkBox.isChecked()) {
                mHabitsDatabase.updateHabit(habit, 1);
                mHabitList.get(position).setDoneMarker(true);
                habitViewHolder.txtTitle.setTextColor(Color.GRAY);
            } else {
                mHabitsDatabase.updateHabit(habit, 0);
                mHabitList.get(position).setDoneMarker(false);
                habitViewHolder.txtTitle.setTextColor(Color.BLACK);
            }
        });

        Calendar calendar = Calendar.getInstance();

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
    public void onItemDismiss(int position) {
        mMovableList.onItemDismiss(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mMovableList.onItemMove(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
}