package com.doapps.habits.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

object AvatarData : LiveData<Uri>() {
  private var value: Uri? = null
  /**
   * TAG is defined for logging errors and debugging information
   */
  private val TAG = AvatarData::class.java.simpleName

  @Deprecated("Use getValue(Context) for local copy optimization", replaceWith = ReplaceWith("AvatarData.getValue(applicationContext)", "android.content.Context"))
  override fun getValue() = when (value) {
    null -> FirebaseAuth.getInstance().currentUser?.photoUrl
    else -> value
  }


  fun getValue(context: Context): Uri? {
    if (value == null) {
      val user = FirebaseAuth.getInstance().currentUser
      if (user != null) {
        val avatarLocalPath = context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null)
        value = when (avatarLocalPath) {
          null -> user.photoUrl
          else -> Uri.parse(avatarLocalPath)
        }
      }

    }
    return value
  }

  public override fun setValue(value: Uri) {
    this.value = value
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null && value != user.photoUrl) {
      val changeRequest = UserProfileChangeRequest.Builder().setPhotoUri(value).build()
      user.updateProfile(changeRequest)
          .addOnFailureListener { e -> Log.e("fail", e.message) }
    } else {
      Log.i(TAG, "URL hasn't changed")
    }
    Log.i(TAG, "New value is " + value)
    super.setValue(value)
  }
}
