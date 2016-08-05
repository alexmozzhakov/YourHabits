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

import com.doapps.habits.R;
import com.doapps.habits.fragments.ProgramsFragment;
import com.doapps.habits.models.ProgramViewProvider;
import com.doapps.habits.viewholder.ProgramViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProgramRecycleAdapter extends RecyclerView.Adapter {

    private final List<ProgramViewProvider> mProgramList;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final ProgramsFragment programsFragment;
    private ImageView mImageTop;
    private TextView mTitleTop;
    private TextView mSuccessTop;

    public ProgramRecycleAdapter(final List<ProgramViewProvider> data, final Context context,
                                 final FragmentManager fragmentManager,
                                 final ProgramsFragment programsFragment) {
        mProgramList = data;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.programsFragment = programsFragment;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_list_item, parent, false);

        final Typeface face = Typeface.createFromAsset(parent.getContext().getAssets(),
                "Montserrat-Regular.ttf");

        final Typeface faceLight = Typeface.createFromAsset(parent.getContext().getAssets(),
                "Montserrat-Light.otf");

        final TextView txtTitle = (TextView) view.findViewById(R.id.program_title);
        final TextView txtPercent = (TextView) view.findViewById(R.id.program_percent);

        txtPercent.setTypeface(face);
        txtTitle.setTypeface(faceLight);

        mTitleTop = (TextView) parent.getRootView().findViewById(R.id.titleTop);
        mImageTop = (ImageView) parent.getRootView().findViewById(R.id.imageViewTop);
        mSuccessTop = (TextView) parent.getRootView().findViewById(R.id.percentTop);

        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,
                                 final int position) {

        final ProgramViewHolder programViewHolder = (ProgramViewHolder) holder;
        final ProgramViewProvider program = mProgramList.get(position);

        programViewHolder.getTitleTextView().setText(program.getTitle());
        programViewHolder.getPercentTextView().setText(program.getPercent());
        programViewHolder.getTitleTextView().setOnClickListener(v -> {
            Picasso.with(context.getApplicationContext())
                    .load(program.getImageLink())
                    .into(mImageTop);
            mTitleTop.setText(program.getTitle());
            mSuccessTop.setVisibility(View.VISIBLE);
            mSuccessTop.setText(program.getSnapshot().child("success").getValue(String.class));
            programsFragment.onClick(program.getSnapshot(),
                    fragmentManager);
        });
    }

    @Override
    public int getItemCount() {
        return mProgramList.size();
    }

}