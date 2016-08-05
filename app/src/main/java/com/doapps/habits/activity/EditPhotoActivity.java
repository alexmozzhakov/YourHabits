package com.doapps.habits.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.yongchun.library.view.ImageSelectorActivity;

import java.util.Arrays;
import java.util.Collection;

public class EditPhotoActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            final Collection<String> images =
                    (Collection<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            // do something
            Log.i("images", Arrays.toString(images.toArray()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
        } else {
            Toast.makeText(this, "This function needs read/write permission", Toast.LENGTH_SHORT).show();
        }
    }


}
