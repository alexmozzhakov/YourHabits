package com.yongchun.library.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropUtil {

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    public static void closeSilently(@Nullable final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (final IOException ignored) {
            // ignored
        } catch (final Throwable ignored) {
            // ignored
        }
    }

    @Nullable
    public static File getFromMediaUri(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        }
        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        }
        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = cursor.getColumnIndex(uri.toString()
                            .startsWith("content://com.google.android.gallery3d") ?
                            MediaStore.MediaColumns.DISPLAY_NAME : MediaStore.MediaColumns.DATA);
                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        final String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (final IllegalArgumentException ignored) {
                // ignored
                return getFromMediaUriPfd(context, resolver, uri);
            } catch (final SecurityException ignored) {
                // ignored
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    private static String getTempFilename(final Context context) throws IOException {
        final File outputDir = context.getCacheDir();
        final File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    @Nullable
    private static File getFromMediaUriPfd(final Context context, final ContentResolver resolver,
                                           final Uri uri) {
        if (uri == null) {
            return null;
        }

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            final ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            final FileDescriptor fd = pfd != null ? pfd.getFileDescriptor() : null;
            if (fd != null) {
                input = new FileInputStream(fd);

                final String tempFilename = getTempFilename(context);
                output = new FileOutputStream(tempFilename);

                final byte[] bytes = new byte[4096];
                int read = input.read(bytes);
                while (read != -1) {
                    output.write(bytes, 0, read);
                    read = input.read(bytes);
                }
                return new File(tempFilename);
            }
        } catch (final FileNotFoundException ignored) {
            // ignored
        } catch (final IOException ignored) {
            // ignored
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }
}
