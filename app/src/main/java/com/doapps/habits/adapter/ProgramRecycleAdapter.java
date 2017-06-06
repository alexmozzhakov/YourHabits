package com.doapps.habits.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.helper.ClickableDataSnapshotFragment;
import com.doapps.habits.models.IProgramViewProvider;
import com.doapps.habits.view.holders.ProgramViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProgramRecycleAdapter extends RecyclerView.Adapter<ProgramViewHolder> {
    private final List<IProgramViewProvider> mProgramList;
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final ClickableDataSnapshotFragment mFragment;
    private ImageView mImageTop;
    private TextView mTitleTop;
    private TextView mSuccessTop;

    public ProgramRecycleAdapter(List<IProgramViewProvider> data, Context context,
                                 FragmentManager fragmentManager,
                                 ClickableDataSnapshotFragment fragment) {
        mProgramList = data;
        mContext = context;
        mFragmentManager = fragmentManager;
        mFragment = fragment;
    }

    @Override
    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.program_list_item, parent, false);

        mTitleTop = parent.getRootView().findViewById(R.id.titleTop);
        mImageTop = parent.getRootView().findViewById(R.id.imageViewTop);
        mSuccessTop = parent.getRootView().findViewById(R.id.percentTop);

        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProgramViewHolder holder, int position) {
        IProgramViewProvider program = mProgramList.get(position);
        holder.getTitleTextView().setText(program.getTitle());
        holder.getPercentTextView().setText(program.getPercent());
        holder.getTitleTextView().setOnClickListener(v -> {
            Picasso.with(mContext.getApplicationContext())
                    .load(program.getImageLink())
                    .into(mImageTop);
            mTitleTop.setText(program.getTitle());
            mSuccessTop.setVisibility(View.VISIBLE);
            mSuccessTop.setText(program.getSnapshot().child("success").getValue(String.class));
            mFragment.onClick(program.getSnapshot(), mFragmentManager);
        });
    }

    @Override
    public int getItemCount() {
        return mProgramList.size();
    }

}