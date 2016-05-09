package com.habit_track.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.habit_track.R;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public final TextView nameLeft;
    public final TextView nameRight;
    public final TimeLineView mTimelineView;

    public TimeLineViewHolder(final View itemView, final int viewType) {
        super(itemView);
        nameLeft = (TextView) itemView.findViewById(R.id.habit_title_left);
        nameRight = (TextView) itemView.findViewById(R.id.habit_title_right);
        mTimelineView = (TimeLineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }

}
