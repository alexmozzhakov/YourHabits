package com.doapps.habits.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doapps.habits.R;
import com.github.vipulasri.timelineview.TimelineView;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.habit_title);
        TimelineView mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}