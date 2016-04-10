package com.habit_track.helper;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.app.Habit;
import com.habit_track.app.ItemTouchHelperAdapter;
import com.habit_track.fragments.ListFragment;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RecycleHabitAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    final private List<Habit> habitList;

    private final static int FADE_DURATION = 1000; // in milliseconds

    public LayoutInflater inflater;

    public RecycleHabitAdapter(final List<Habit> data) {
        super();
        this.habitList = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.habit_title);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        }
    }

    public void add(int position, Habit item) {
        habitList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Habit item) {
        int position = habitList.indexOf(item);
        habitList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RecycleHabitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rholder, int position) {

        ViewHolder holder = (ViewHolder) rholder;
        holder.txtTitle.setText(habitList.get(position).title);
        final Habit habit = habitList.get(position);
        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                habitList.get(position).setDoneMarker(true);

                ListFragment.habitsDatabase.updateHabit(
                        habit.id,
                        habit.markerUpdatedDay,
                        habit.markerUpdatedMonth,
                        habit.markerUpdatedYear,
                        1);

                holder.txtTitle.setTextColor(Color.GRAY);
            } else {

                ListFragment.habitsDatabase.updateHabit(
                        habit.id,
                        habit.markerUpdatedDay,
                        habit.markerUpdatedMonth,
                        habit.markerUpdatedYear,
                        0);

                holder.txtTitle.setTextColor(Color.BLACK);
                habitList.get(position).setDoneMarker(false);
            }
        });

        Calendar calendar = Calendar.getInstance();

        holder.checkBox.setChecked(habit.isDone(
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)));

        //setScaleAnimation(holder.txtTitle);

    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public void onItemDismiss(int position) {
        habitList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(habitList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(habitList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }
}