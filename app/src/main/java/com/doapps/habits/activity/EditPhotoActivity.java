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
import com.doapps.habits.BuildConfig;
import com.doapps.habits.helper.AvatarManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPhotoActivity extends Activity {

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String UPLOAD_URL = "http://habbitsapp.esy.es/upload.php";
    private static final String KEY_IMAGE = "image";
    private ProgressDialog prgDialog;
    private Bitmap bitmap;
    private static final String TAG = EditPhotoActivity.class.getSimpleName();
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            FacebookSdk.sdkInitialize(getApplicationContext());
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
            uploadImage();
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading =
                ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                s -> {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (user.getPhotoUrl() != null &&
                                user.getPhotoUrl().toString().contains("facebook")) {
                            logInWithFacebook();
                            AvatarManager.listener.setUri(Uri.parse(s + "&facebook"));
                        } else {
                            AvatarManager.listener.setUri(Uri.parse(s));
                        }

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
                // ignored
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

    private void logInWithFacebook() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult result) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "facebook:onSuccess:" + result);
                        }
                        handleFacebookAccessToken(result.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(final FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }

                    private void handleFacebookAccessToken(final AccessToken token) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "handleFacebookAccessToken:" + token);
                        }

                        final FirebaseUser[] user = {FirebaseAuth.getInstance().getCurrentUser()};
                        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                        user[0].reauthenticate(credential)
                                .addOnCompleteListener(task -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                                    }

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (task.isSuccessful()) {
                                        user[0] = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user[0] != null && user[0].getPhotoUrl() == null) {
                                            final String userID = token.getUserId();
                                            final UserProfileChangeRequest profileUpdates =
                                                    new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(Uri.parse(String.format("https://graph.facebook.com/%s/picture?type=large", userID)))
                                                            .build();

                                            user[0].updateProfile(profileUpdates)
                                                    .addOnCompleteListener(update -> {
                                                        if (update.isSuccessful()) {
                                                            Log.d(TAG, "User photo set.");
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.w(TAG, "signInWithCredential", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}
