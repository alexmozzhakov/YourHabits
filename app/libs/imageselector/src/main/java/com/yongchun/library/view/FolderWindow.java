package com.yongchun.library.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.yongchun.library.R;
import com.yongchun.library.adapter.ImageFolderAdapter;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.model.LocalMediaFolder;
import com.yongchun.library.model.OnItemClickListener;
import com.yongchun.library.utils.ScreenUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class FolderWindow extends PopupWindow {
    private final Context context;
    private final View window;
    private RecyclerView recyclerView;
    private ImageFolderAdapter adapter;
    private boolean isDismiss;

    FolderWindow(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.window_folder, null);
        setContentView(window);
        setWidth(ScreenUtils.getScreenWidth(context));
        setHeight(ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2px(context, 96));
        setAnimationStyle(R.style.WindowStyle);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(Color.argb(153, 0, 0, 0)));

        initView();
        setPopupWindowTouchModal(this, false);
    }

    private static void setPopupWindowTouchModal(PopupWindow popupWindow,
                                                 boolean touchModal) {
        if (popupWindow == null) {
            return;
        }
        try {
            Method method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (IllegalAccessException e) {
            Log.e("FolderWindow", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e("FolderWindow", e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e("FolderWindow", e.getMessage());
        } catch (SecurityException e) {
            Log.e("FolderWindow", e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e("FolderWindow", e.getMessage());
        } catch (RuntimeException e) {
            Log.e("FolderWindow", e.getMessage());
        }
    }

    private void initView() {
        adapter = new ImageFolderAdapter(context);

        recyclerView = (RecyclerView) window.findViewById(R.id.folder_list);
        recyclerView.addItemDecoration(new ItemDivider(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    void bindFolder(List<LocalMediaFolder> folders) {
        adapter.bindFolder(folders);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_in);
        recyclerView.startAnimation(animation);
    }

    void setOnItemClickListener(OnItemClickListener<LocalMedia> onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.down_out);
        recyclerView.startAnimation(animation);
        dismiss();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // ignored
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                FolderWindow.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // ignored
            }
        });
    }
}
