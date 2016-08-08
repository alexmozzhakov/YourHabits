package com.doapps.habits.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class EditPhotoActivity extends Activity {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String UPLOAD_URL = "http://habbitsapp.esy.es/upload.php";
    private static final String KEY_IMAGE = "image";
    private ProgressDialog prgDialog;
    private Bitmap bitmap;

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
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            final List<String> images =
                    (List<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

            bitmap = BitmapFactory.decodeFile(images.get(0));
            Log.i("some", String.valueOf(bitmap.getByteCount()));
            uploadImageV2();
        }
    }

    private void uploadImageV2() {
        //Showing the progress dialog
        final ProgressDialog loading =
                ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                s -> {
                    //Dismissing the progress dialog
                    //Showing toast message of the response
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Log.i("Url", String.valueOf(Uri.parse(s)));
                        if (user.getPhotoUrl() != null &&
                                user.getPhotoUrl().toString().contains("facebook")) {
                            s += "?facebook";
                        }
                        final UserProfileChangeRequest changeRequest =
                                new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(s)).build();
                        user.updateProfile(changeRequest).addOnCompleteListener(EditPhotoActivity.this,
                                task -> {
                                });
                        user.updateProfile(changeRequest).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            } else {
                                task.addOnFailureListener(e -> Log.e("ERROR", e.getMessage()));
                                Toast.makeText(EditPhotoActivity.this, "Error updating profile",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(EditPhotoActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                    loading.dismiss();
                    finish();
                },
                volleyError -> {
                    //Dismissing the progress dialog
                    loading.dismiss();

                    //Showing toast
                    Toast.makeText(EditPhotoActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", volleyError.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                //Creating parameters
                final Map<String, String> params = new HashMap<>(1);

                //Adding parameters
                params.put(KEY_IMAGE, getStringImage(bitmap));

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
            public void retry(final VolleyError error) {
                // ignore
            }
        });
        //Creating a Request Queue
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public static String getStringImage(final Bitmap bmp) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] imageBytes = stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            ImageSelectorActivity.start(this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
        } else {
            Toast.makeText(this, "This function needs read/write permission", Toast.LENGTH_SHORT).show();
        }
    }


}
