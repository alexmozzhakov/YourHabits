package com.habit_track.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.helper.TimeLineView;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public final TextView name;

    public TimeLineViewHolder(final View itemView, final int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.habit_title);
        TimeLineView timeLineView = (TimeLineView) itemView.findViewById(R.id.time_marker);
        timeLineView.initLine(viewType);
    }
}
