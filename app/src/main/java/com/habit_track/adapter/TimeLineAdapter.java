package com.habit_track.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.helper.TimeLineView;
import com.habit_track.models.Habit;
import com.habit_track.viewholder.TimeLineViewHolder;

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
        final View view = View.inflate(parent.getContext(), R.layout.habit_timeline_item, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) {
        holder.name.setText(mFeedList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

}