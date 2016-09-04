package com.doapps.habits.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doapps.habits.R;
import com.doapps.habits.fragments.ProgramsFragment;
import com.doapps.habits.models.IProgramViewProvider;
import com.doapps.habits.viewholder.ProgramViewHolder;

import java.util.List;

public class ProgramRecycleAdapter extends RecyclerView.Adapter {
    private final List<IProgramViewProvider> mProgramList;
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final ProgramsFragment mProgramsFragment;
    private ImageView mImageTop;
    private TextView mTitleTop;
    private TextView mSuccessTop;

    public ProgramRecycleAdapter(List<IProgramViewProvider> data, Context context,
                                 FragmentManager fragmentManager,
                                 ProgramsFragment programsFragment) {
        mProgramList = data;
        mContext = context;
        mFragmentManager = fragmentManager;
        mProgramsFragment = programsFragment;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_list_item, parent, false);

        Typeface face = Typeface.createFromAsset(parent.getContext().getAssets(),
                "Montserrat-Regular.ttf");

        Typeface faceLight = Typeface.createFromAsset(parent.getContext().getAssets(),
                "Montserrat-Light.otf");

        TextView txtTitle = (TextView) view.findViewById(R.id.program_title);
        TextView txtPercent = (TextView) view.findViewById(R.id.program_percent);

        txtPercent.setTypeface(face);
        txtTitle.setTypeface(faceLight);

        mTitleTop = (TextView) parent.getRootView().findViewById(R.id.titleTop);
        mImageTop = (ImageView) parent.getRootView().findViewById(R.id.imageViewTop);
        mSuccessTop = (TextView) parent.getRootView().findViewById(R.id.percentTop);

        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {

        ProgramViewHolder programViewHolder = (ProgramViewHolder) holder;
        IProgramViewProvider program = mProgramList.get(position);

        programViewHolder.getTitleTextView().setText(program.getTitle());
        programViewHolder.getPercentTextView().setText(program.getPercent());
        programViewHolder.getTitleTextView().setOnClickListener(v -> {
            Glide.with(mContext.getApplicationContext())
                    .load(program.getImageLink())
                    .into(mImageTop);
            mTitleTop.setText(program.getTitle());
            mSuccessTop.setVisibility(View.VISIBLE);
            mSuccessTop.setText(program.getSnapshot().child("success").getValue(String.class));
            // TODO: 11/08/2016 change onClick to something more abstract
            mProgramsFragment.onClick(program.getSnapshot(), mFragmentManager);
        });
    }

    @Override
    public int getItemCount() {
        return mProgramList.size();
    }

}