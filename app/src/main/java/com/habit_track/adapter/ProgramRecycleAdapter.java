package com.habit_track.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.models.ProgramView;
import com.habit_track.viewholder.ProgramViewHolder;

import java.util.List;

public class ProgramRecycleAdapter extends RecyclerView.Adapter {

    final private List<ProgramView> mProgramList;

    public ProgramRecycleAdapter(final List<ProgramView> data) {
        super();
        this.mProgramList = data;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_list_item, parent, false);

        final Typeface face = Typeface.createFromAsset(parent.getContext().getAssets(),
                "fonts/Montserrat-Regular.ttf");

        final Typeface faceLight = Typeface.createFromAsset(parent.getContext().getAssets(),
                "fonts/Montserrat-Light.otf");

        final TextView txtTitle = (TextView) view.findViewById(R.id.program_title);
        final TextView txtPercent = (TextView) view.findViewById(R.id.program_percent);

        txtPercent.setTypeface(face);
        txtTitle.setTypeface(faceLight);
        txtPercent.setTextColor(Color.parseColor("#801D1D26"));

        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder recyclerHolder, final int position) {

        final ProgramViewHolder holder = (ProgramViewHolder) recyclerHolder;
        final ProgramView program = mProgramList.get(position);

        holder.txtTitle.setText(program.title);
        holder.txtPercent.setText(program.percent);
    }

    @Override
    public int getItemCount() {
        return mProgramList.size();
    }

}