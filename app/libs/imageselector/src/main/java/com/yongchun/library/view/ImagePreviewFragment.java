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

    public static ImagePreviewFragment getInstance(final String path) {
        final ImagePreviewFragment fragment = new ImagePreviewFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        final ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        final String path = getArguments().getString(PATH);
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

        private BitmapSimpleTarget(final ImageView imageView, final PhotoViewAttacher mAttacher) {
            super(480, 800);
            this.imageView = imageView;
            this.mAttacher = mAttacher;
        }

        @Override
        public void onResourceReady(final Bitmap resource,
                                    final GlideAnimation<? super Bitmap> glideAnimation) {
            imageView.setImageBitmap(resource);
            mAttacher.update();
        }
    }
}
