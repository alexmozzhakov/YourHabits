package com.yongchun.library.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.yongchun.library.R;
import com.yongchun.library.utils.CropUtil;
import com.yongchun.library.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageCropActivity extends AppCompatActivity {
    static final String OUTPUT_PATH = "outputPath";
    static final int REQUEST_CROP = 69;
    private static final String EXTRA_PATH = "extraPath";
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    private final Handler handler = new Handler();
    private Toolbar toolbar;
    private TextView doneText;
    private CropImageView cropImageView;
    private Uri sourceUri;
    private Uri saveUri;

    static void startCrop(Activity activity, String path) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        activity.startActivityForResult(intent, REQUEST_CROP);
    }

    private static int getExifRotation(File imageFile) {
        if (imageFile == null) {
            return 0;
        }
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            // We only recognize a subset of orientation tag values
            int i = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            if (i == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (i == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (i == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            } else {
                return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    private static Matrix getRotateMatrix(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        if (bitmap != null && rotation != 0) {
            int cx = bitmap.getWidth() / 2;
            int cy = bitmap.getHeight() / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(rotation);
            matrix.postTranslate(bitmap.getWidth() >> 1, bitmap.getHeight() >> 1);
        }
        return matrix;
    }

    private static int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        return textureLimit == 0 ? SIZE_DEFAULT : Math.min(textureLimit, SIZE_LIMIT);
    }

    private static int getMaxTextureSize() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        sourceUri = Uri.fromFile(new File(path));

        initView();
        registerListener();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);

        doneText = (TextView) findViewById(R.id.done_text);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setHandleSizeInDp(10);


        int exifRotation =
                getExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri));

        InputStream is = null;
        try {
            int sampleSize = calculateBitmapSampleSize(sourceUri);
            is = getContentResolver().openInputStream(sourceUri);
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = sampleSize;
            Bitmap sizeBitmap = BitmapFactory.decodeStream(is, null, option);
            if (sizeBitmap == null) {
                return;
            }
            Matrix matrix = getRotateMatrix(sizeBitmap, exifRotation % 360);
            Bitmap rotated =
                    Bitmap.createBitmap(sizeBitmap, 0, 0, sizeBitmap.getWidth(),
                            sizeBitmap.getHeight(), matrix, true);
            cropImageView.setImageBitmap(rotated);
        } catch (FileNotFoundException e) {
            Log.e("ImageCrop", e.getMessage());
        } finally {
            CropUtil.closeSilently(is);
        }
    }

    private void registerListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUri = Uri.fromFile(FileUtils.createCropFile(ImageCropActivity.this));
                saveOutput(cropImageView.getCroppedBitmap());
            }
        });
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            CropUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize <<= 1;
        }
        return sampleSize;
    }

    private void saveOutput(final Bitmap croppedImage) {
        if (saveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(saveUri);
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (FileNotFoundException e) {
                Log.e("ImageCrop", e.getMessage());
            } finally {
                CropUtil.closeSilently(outputStream);
            }
            setResult(RESULT_OK, new Intent().putExtra(OUTPUT_PATH, saveUri.getPath()));
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                croppedImage.recycle();
            }
        });
        finish();
    }
}
