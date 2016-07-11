package com.doapps.habits.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.doapps.habits.R;


public class HabitViewHolder extends RecyclerView.ViewHolder {

    public final TextView txtTitle;
    public final CheckBox checkBox;

    public HabitViewHolder(final View itemView) {
        super(itemView);
        txtTitle = (TextView) itemView.findViewById(R.id.habit_title);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
    }
}