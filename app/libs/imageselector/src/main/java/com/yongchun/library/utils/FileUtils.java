package com.yongchun.library.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    private static final String POSTFIX = ".JPEG";
    private static final String APP_NAME = "ImageSelector";
    private static final String CAMERA_PATH = "/" + APP_NAME + "/CameraImage/";
    private static final String CROP_PATH = "/" + APP_NAME + "/CropImage/";

    public static File createCameraFile(Context context) {
        return createMediaFile(context, CAMERA_PATH);
    }

    public static File createCropFile(Context context) {
        return createMediaFile(context, CROP_PATH);
    }

    private static File createMediaFile(Context context, String parentPath) {
        String state = Environment.getExternalStorageState();
        File rootDir = state.equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory() : context.getCacheDir();

        File folderDir = new File(rootDir.getAbsolutePath() + parentPath);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = APP_NAME + '_' + timeStamp;
        return new File(folderDir, fileName + POSTFIX);
    }
}
