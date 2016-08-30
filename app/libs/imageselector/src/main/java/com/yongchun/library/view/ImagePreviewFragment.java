package com.yongchun.library.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yongchun.library.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewFragment extends Fragment {
    private static final String PATH = "path";

    public static ImagePreviewFragment getInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        String path = getArguments().getString(PATH);
        if (path != null) {
            Glide.with(container.getContext())
                    .load(new File(path))
                    .asBitmap()
                    .into(new ImagePreviewFragment.BitmapSimpleTarget(imageView, mAttacher));
        }
        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((ImagePreviewActivity) getActivity()).switchBarVisibility();
            }
        });
        return contentView;
    }

    private static class BitmapSimpleTarget extends SimpleTarget<Bitmap> {
        private final ImageView imageView;
        private final PhotoViewAttacher mAttacher;

        private BitmapSimpleTarget(ImageView imageView, PhotoViewAttacher mAttacher) {
            super(480, 800);
            this.imageView = imageView;
            this.mAttacher = mAttacher;
        }

        @Override
        public void onResourceReady(Bitmap resource,
                                    GlideAnimation<? super Bitmap> glideAnimation) {
            imageView.setImageBitmap(resource);
            mAttacher.update();
        }
    }
}
