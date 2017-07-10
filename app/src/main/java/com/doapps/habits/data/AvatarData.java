package com.doapps.habits.data;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AvatarData extends LiveData<Uri> {

  /**
   * TAG is defined for logging errors and debugging information
   */
  private static final String TAG = AvatarData.class.getSimpleName();
  private static final AvatarData instance = new AvatarData();
  private Uri uri;

  public static AvatarData getInstance() {
    return instance;
  }

  @Override
  public Uri getValue() {
    if (uri == null) {
      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      if (user != null) {
        uri = user.getPhotoUrl();
      }

    }
    return uri;
  }

  @Override
  public void setValue(Uri value) {
    uri = value;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null && !uri.equals(user.getPhotoUrl())) {
      UserProfileChangeRequest changeRequest =
          new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
      user.updateProfile(changeRequest)
          .addOnFailureListener(e -> Log.e("fail", e.getMessage()));
    } else {
      Log.i(TAG, "URL hasn't changed");
    }
    Log.i(TAG, "New uri is " + uri);
    super.setValue(value);
  }
}
