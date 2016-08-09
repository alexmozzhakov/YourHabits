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

/**
 * Created by dee on 15/11/19.
 */
public class LocalMediaLoader {
    // load type
    public static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private final Collection<String> mDirPaths = new HashSet<>(1);

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
    private int type = TYPE_IMAGE;

    private final FragmentActivity activity;
    public LocalMediaLoader(final FragmentActivity activity, final int type) {
        this.activity = activity;
        this.type = type;
    }


    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
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
            public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
                final List<LocalMediaFolder> imageFolders = new ArrayList<>();
                final LocalMediaFolder allImageFolder = new LocalMediaFolder();
                final List<LocalMedia> allImages = new ArrayList<>();

                while (data != null && data.moveToNext()) {
                    // Get picture path
                    final String path = data.getString(data
                            .getColumnIndex(MediaStore.MediaColumns.DATA));
                    final File file = new File(path);
                    if (!file.exists()) {
                        continue;
                    }
                    // Get the picture directory path name
                    final File parentFile = file.getParentFile();
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
                    final LocalMediaFolder localMediaFolder = getImageFolder(path, imageFolders);

                    final File[] files = parentFile.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg");
                        }
                    });
                    final List<LocalMedia> images = new ArrayList<>(files.length);
                    for (final File image : files) {
                        final LocalMedia localMedia = new LocalMedia(image.getAbsolutePath());
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
            public void onLoaderReset(final Loader<Cursor> loader) {
                // ignored
            }
        });
    }

    private static void sortFolder(final List<LocalMediaFolder> imageFolders) {
        // Folder of images sorted by image number
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(final LocalMediaFolder t, final LocalMediaFolder t1) {
                if (t.getImages() == null || t1.getImages() == null) {
                    return 0;
                }
                final int lsize = t.getImageNum();
                final int rsize = t1.getImageNum();
                if (lsize < rsize) {
                    return 1;
                } else {
                    return lsize == rsize ? 0 : -1;
                }
            }
        });
    }

    private static LocalMediaFolder getImageFolder(final String path, final Iterable<LocalMediaFolder> imageFolders) {
        final File imageFile = new File(path);
        final File folderFile = imageFile.getParentFile();

        for (final LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        final LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        return newFolder;
    }

}
