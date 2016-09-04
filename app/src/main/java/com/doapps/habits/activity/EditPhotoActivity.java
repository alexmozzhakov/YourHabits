package com.doapps.habits.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.doapps.habits.helper.AvatarManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPhotoActivity extends Activity {
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String UPLOAD_URL = "http://habbitsapp.esy.es/upload.php";
    private static final String KEY_IMAGE = "image";
    private Bitmap bitmap;
    private static final String TAG = EditPhotoActivity.class.getSimpleName();
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            List<String> images =
                    (List<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

            bitmap = BitmapFactory.decodeFile(images.get(0));
            Log.i(TAG, String.valueOf(bitmap.getByteCount()));
            uploadImage();
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage() {
        Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                s -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Toast.makeText(EditPhotoActivity.this, "Upload complete"
                                , Toast.LENGTH_LONG).show();
                        AvatarManager.listener.setUri(Uri.parse(s), this);
                    } else {
                        Toast.makeText(EditPhotoActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(EditPhotoActivity.this,
                            "Server error " + volleyError.networkResponse.statusCode,
                            Toast.LENGTH_LONG).show();
                }) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected Map<String, String> getParams() {
                //Creating parameters
                Map<String, String> params = new HashMap<>(1);

                //Adding parameters
                params.put(KEY_IMAGE, getStringImage(bitmap));
                params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) {
                // ignored
            }
        });
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        finish();
    }

    private static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
        } else {
            Toast.makeText(this, "This function needs read/write permission", Toast.LENGTH_SHORT).show();
        }
    }
}
