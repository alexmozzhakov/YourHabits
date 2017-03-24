package com.doapps.habits.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IUpdatableList;
import com.doapps.habits.viewholder.TimeLineViewHolder;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;


public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements IUpdatableList<Habit> {

    private List<Habit> mFeedList;

    public TimeLineAdapter(List<Habit> feedList) {
        mFeedList = feedList;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View view = mLayoutInflater.inflate(R.layout.habit_timeline_item, parent, false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        Habit habit = mFeedList.get(position);
        holder.textView.setText(habit.title);
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    @Override
    public void updateList(List<Habit> data) {
        mFeedList = data;
        notifyDataSetChanged();
    }
}