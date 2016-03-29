package com.habit_track.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.app.Habit;

public class HabitListAdapter extends ArrayAdapter<Habit> {

    Context context;
    int layoutResourceId;
    Habit habitList[] = null;

    public HabitListAdapter(Context context, int layoutResourceId, Habit[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.habitList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProgramHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProgramHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.prog_title);
          //  holder.txtPercent = (TextView)row.findViewById(R.id.prog_percent);

            row.setTag(holder);
        }
        else
        {
            holder = (ProgramHolder)row.getTag();
        }

        Habit habit = habitList[position];

        final Typeface face = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular.ttf");
        final Typeface faceLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Light.otf");

        holder.txtTitle.  setText(habit.title);
        holder.txtTitle.  setTypeface(faceLight);
//        holder.txtPercent.setTextColor(Color.parseColor("#801D1D26"));
//        holder.txtPercent.setTypeface(face);
//        holder.txtPercent.setText(habit.description);

        return row;
    }

    static class ProgramHolder {
        TextView txtTitle;
        TextView txtPercent;
    }
}