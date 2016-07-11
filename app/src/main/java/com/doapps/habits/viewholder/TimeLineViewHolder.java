package com.doapps.habits.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.helper.TimeLineView;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    private final TextView name;

    public TimeLineViewHolder(final View itemView, final int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.habit_title);
        final TimeLineView timeLineView = (TimeLineView) itemView.findViewById(R.id.time_marker);
        timeLineView.initLine(viewType);
    }

    public TextView getName() {
        return name;
    }
}
