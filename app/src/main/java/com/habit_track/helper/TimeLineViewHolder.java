package com.habit_track.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.habit_track.R;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.habit_title);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }

}
