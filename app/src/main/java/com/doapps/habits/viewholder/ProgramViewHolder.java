package com.doapps.habits.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.doapps.habits.R;

public class ProgramViewHolder extends RecyclerView.ViewHolder {
    private final TextView txtTitle;
    private final TextView txtPercent;

    public ProgramViewHolder(final View itemView) {
        super(itemView);
        txtTitle = (TextView) itemView.findViewById(R.id.program_title);
        txtPercent = (TextView) itemView.findViewById(R.id.program_percent);
    }

    public TextView getTitleTextView() {
        return txtTitle;
    }

    public TextView getPercentTextView() {
        return txtPercent;
    }
}
