package com.dohabit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dohabit.R;
import com.dohabit.helper.TimeLineView;
import com.dohabit.models.Habit;
import com.dohabit.models.Listable;
import com.dohabit.viewholder.TimeLineViewHolder;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements Listable {

    private List<Habit> mFeedList;

    public TimeLineAdapter(final List<Habit> feedList) {
        mFeedList = feedList;
    }

    @Override
    public List<Habit> getList() {
        return mFeedList;
    }

    public void updateList(final List<Habit> data) {
        mFeedList = data;
        notifyDataSetChanged();
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
        holder.getName().setText(mFeedList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return mFeedList != null ? mFeedList.size() : 0;
    }

}