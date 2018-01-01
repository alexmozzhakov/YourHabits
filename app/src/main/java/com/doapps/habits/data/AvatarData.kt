package com.doapps.habits.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.doapps.habits.BuildConfig
import com.doapps.habits.helper.PicassoRoundedTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.squareup.picasso.Picasso
import java.io.File

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

  fun hasAvatar(context: Context): Boolean =
      context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null) != null || FirebaseAuth.getInstance().currentUser?.photoUrl != null

  fun getLocalAvatarUri(context: Context): String? =
      context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null)

  fun getAvatar(context: Context, imageView: ImageView) {
    val localAvatarUri = getLocalAvatarUri(context)
    if (localAvatarUri != null) {
      val optimalFile = File(localAvatarUri)
      if (BuildConfig.DEBUG) Log.i(TAG, localAvatarUri.toString())
      Picasso.with(context)
          .load(optimalFile)
          .transform(PicassoRoundedTransformation())
          .fit().centerInside()
          .into(imageView)
      imageView.visibility = View.VISIBLE
    } else {
      val user = FirebaseAuth.getInstance().currentUser
      if (user != null) {
        value = user.photoUrl
        if (value != null) {
          Picasso.with(context)
              .load(value)
              .transform(PicassoRoundedTransformation())
              .into(imageView)
          imageView.visibility = View.VISIBLE
        }
      }
    }
  }

  public override fun setValue(value: Uri) {
    this.value = value
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null && value != user.photoUrl) {
      val changeRequest = UserProfileChangeRequest.Builder().setPhotoUri(value).build()
      user.updateProfile(changeRequest)
          .apply { if (BuildConfig.DEBUG) addOnFailureListener { e -> Log.e("fail", e.message) } }
    } else {
      Log.i(TAG, "URL hasn't changed")
    }
    Log.i(TAG, "New value is " + value)
    super.setValue(value)
  }

  fun clear(context: Context) {
    this.value = null
    context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit().remove("avatar").apply()
  }
}
