package com.habit_track.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.app.Habit;
import com.habit_track.fragments.ListFragment;

import java.util.Calendar;
import java.util.List;

public class HabitListAdapter extends ArrayAdapter<Habit> {

    final private int layoutResourceId;
    final private List<Habit> habitList;
    final private Context context;

    public HabitListAdapter(final Context context, final int layoutResourceId, final List<Habit> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.habitList = data;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View row = convertView;
        ProgramHolder holder;
        final Habit habit = habitList.get(position);

        if (row == null) {
            final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProgramHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.habit_title);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);
            //      holder.checkBox.setChecked(ListFragment.habitList.get(position).doneMarker);
            holder.checkBox.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()) {
                    Log.i("****", "captured " + (position + 1));
                    //ListFragment.habitsDatabase.deleteHabit(ListFragment.habitList.get(position).id);
                    //ListFragment.habitList.remove(position);

                    habitList.get(position).setDoneMarker(true);
                    ListFragment.habitsDatabase.updateHabit(habit.id, habit.markerUpdatedDay, habit.markerUpdatedMonth, habit.markerUpdatedYear);
                    holder.txtTitle.setTextColor(Color.GRAY);
                } else {
                    holder.txtTitle.setTextColor(Color.BLACK);
                }
            });

            row.setTag(holder);
        } else {
            holder = (ProgramHolder) row.getTag();
        }

        final Calendar calendar = Calendar.getInstance();

        holder.txtTitle.setText(habit.title);
        boolean checked = habit.isDone(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        if(checked) {
            holder.checkBox.setChecked(true);
            holder.txtTitle.setTextColor(Color.GRAY);
        }

        return row;
    }

    static class ProgramHolder {
        private TextView txtTitle;
        private CheckBox checkBox;
    }
}