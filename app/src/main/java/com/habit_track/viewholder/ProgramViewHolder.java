package com.habit_track.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.habit_track.R;

public class ProgramViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTitle;
    public TextView txtPercent;

    public ProgramViewHolder(View itemView) {
        super(itemView);
        txtTitle = (TextView) itemView.findViewById(R.id.program_title);
        txtPercent = (TextView) itemView.findViewById(R.id.program_percent);
    }
}
