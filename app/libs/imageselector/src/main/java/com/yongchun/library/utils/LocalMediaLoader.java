package com.yongchun.library.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.model.LocalMediaFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class LocalMediaLoader {
    // load type
    public static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final String[] IMAGE_PROJECTION = {
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            BaseColumns._ID};
    private static final String[] VIDEO_PROJECTION = {
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            BaseColumns._ID,
            MediaStore.Video.VideoColumns.DURATION};
    private final Collection<String> mDirPaths = new HashSet<>(1);
    private final FragmentActivity activity;
    private int type = TYPE_IMAGE;

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    private static void sortFolder(List<LocalMediaFolder> imageFolders) {
        // Folder of images sorted by image number
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(LocalMediaFolder t, LocalMediaFolder t1) {
                if (t.getImages() == null || t1.getImages() == null) {
                    return 0;
                }
                int lSize = t.getImageNum();
                int rSize = t1.getImageNum();
                if (lSize < rSize) {
                    return 1;
                } else {
                    return lSize == rSize ? 0 : -1;
                }
            }
        });
    }

    private static LocalMediaFolder getImageFolder(String path, Iterable<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        return newFolder;
    }

    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == TYPE_IMAGE) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION, MediaStore.MediaColumns.MIME_TYPE + "=? or "
                            + MediaStore.MediaColumns.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
                } else if (id == TYPE_VIDEO) {
                    cursorLoader = new CursorLoader(
                            activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                List<LocalMediaFolder> imageFolders = new ArrayList<>();
                LocalMediaFolder allImageFolder = new LocalMediaFolder();
                List<LocalMedia> allImages = new ArrayList<>();

                while (data != null && data.moveToNext()) {
                    // Get picture path
                    String path = data.getString(data
                            .getColumnIndex(MediaStore.MediaColumns.DATA));
                    File file = new File(path);
                    if (!file.exists()) {
                        continue;
                    }
                    // Get the picture directory path name
                    File parentFile = file.getParentFile();
                    if (parentFile == null || !parentFile.exists()) {
                        continue;
                    }

                    String dirPath = parentFile.getAbsolutePath();
                    // 利用一个HashSet防止多次扫描同一个文件夹
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    }
                    mDirPaths.add(dirPath);

                    if (parentFile.list() == null) {
                        continue;
                    }
                    LocalMediaFolder localMediaFolder = getImageFolder(path, imageFolders);

                    File[] files = parentFile.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg");
                        }
                    });
                    List<LocalMedia> images = new ArrayList<>(files.length);
                    for (File image : files) {
                        LocalMedia localMedia = new LocalMedia(image.getAbsolutePath());
                        allImages.add(localMedia);
                        images.add(localMedia);
                    }
                    if (!images.isEmpty()) {
                        localMediaFolder.setImages(images);
                        localMediaFolder.setImageNum(localMediaFolder.getImages().size());
                        imageFolders.add(localMediaFolder);
                    }
                }

                allImageFolder.setImages(allImages);
                allImageFolder.setImageNum(allImageFolder.getImages().size());
                allImageFolder.setFirstImagePath(allImages.get(0).getPath());
                allImageFolder.setName(activity.getString(com.yongchun.library.R.string.all_image));
                imageFolders.add(allImageFolder);
                sortFolder(imageFolders);
                imageLoadListener.loadComplete(imageFolders);
                if (data != null) {
                    data.close();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                // ignored
            }
        });
    }

}
