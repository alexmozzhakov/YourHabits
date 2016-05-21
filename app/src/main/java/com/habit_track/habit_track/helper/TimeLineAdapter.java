package com.habit_track.habit_track.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.habit_track.app.Habit;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private final List<Habit> mFeedList;

    public TimeLineAdapter(final List<Habit> feedList) {
        super();
        mFeedList = feedList;
    }

    @Override
    public int getItemViewType(final int position) {
        return TimeLineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.habit_timeline_item, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.nameLeft.setText(mFeedList.get(position).title);
        } else {
            holder.nameRight.setText(mFeedList.get(position).title);
        }

    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

}

