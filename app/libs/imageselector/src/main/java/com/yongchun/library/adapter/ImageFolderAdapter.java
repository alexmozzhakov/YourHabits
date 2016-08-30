package com.yongchun.library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yongchun.library.R;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.model.LocalMediaFolder;
import com.yongchun.library.model.OnItemClickListener;
import com.yongchun.library.view.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageFolderAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final Context context;
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private int checkedIndex;

    private OnItemClickListener<LocalMedia> onItemClickListener;

    public ImageFolderAdapter(Context context) {
        this.context = context;
    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocalMediaFolder folder = folders.get(position);
        Glide.with(context)
                .load(new File(folder.getFirstImagePath()))
                .placeholder(R.mipmap.ic_placeholder)
                .error(R.mipmap.ic_placeholder)
                .centerCrop()
                .into(holder.getFirstImage());
        holder.getFolderName().setText(folder.getName());
        holder.getImageNum().setText(context.getString(R.string.num_postfix, folder.getImageNum()));

        holder.getIsSelected().setVisibility(checkedIndex == holder.getAdapterPosition()
                ? View.VISIBLE : View.GONE);

        holder.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    checkedIndex = holder.getAdapterPosition();
                    notifyDataSetChanged();
                    onItemClickListener.onItemClick(folder.getName(), folder.getImages());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void setOnItemClickListener(OnItemClickListener<LocalMedia> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
