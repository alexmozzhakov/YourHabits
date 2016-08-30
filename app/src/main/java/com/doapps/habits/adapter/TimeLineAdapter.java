package com.doapps.habits.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.doapps.habits.R;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.LineType;
import com.doapps.habits.models.UpdatableList;
import com.doapps.habits.viewholder.TimeLineViewHolder;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder>
        implements UpdatableList<Habit> {
    private List<Habit> mFeedList;
    private final int mListSize;

    public TimeLineAdapter(List<Habit> feedList) {
        mFeedList = feedList;
        mListSize = getItemCount();
    }

    private static int getTimeLineViewType(int position, int totalSize) {
        if (totalSize == 1) {
            return LineType.ONLY_ONE;
        } else if (position == 0) {
            return LineType.BEGIN;
        } else if (position == totalSize - 1) {
            return LineType.END;
        } else {
            return LineType.NORMAL;
        }
    }

    @Override
    public void updateList(List<Habit> data) {
        mFeedList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return getTimeLineViewType(position, mListSize);
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.habit_timeline_item, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        holder.getName().setText(mFeedList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return mFeedList != null ? mFeedList.size() : 0;
    }
}