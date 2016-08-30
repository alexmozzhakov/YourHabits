package com.yongchun.library.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yongchun.library.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView firstImage;
    private TextView folderName;
    private TextView imageNum;
    private ImageView isSelected;

    private View contentView;

    public ViewHolder(View itemView) {
        super(itemView);
        contentView = itemView;
        firstImage = (ImageView) itemView.findViewById(R.id.first_image);
        folderName = (TextView) itemView.findViewById(R.id.folder_name);
        imageNum = (TextView) itemView.findViewById(R.id.image_num);
        isSelected = (ImageView) itemView.findViewById(R.id.is_selected);
    }

    public ImageView getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(ImageView firstImage) {
        this.firstImage = firstImage;
    }

    public TextView getFolderName() {
        return folderName;
    }

    public void setFolderName(TextView folderName) {
        this.folderName = folderName;
    }

    public TextView getImageNum() {
        return imageNum;
    }

    public void setImageNum(TextView imageNum) {
        this.imageNum = imageNum;
    }

    public ImageView getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(ImageView isSelected) {
        this.isSelected = isSelected;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }
}