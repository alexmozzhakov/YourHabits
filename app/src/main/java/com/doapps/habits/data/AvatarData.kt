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
  /**
   * An avatar Uri
   */
  private var value: Uri? = null
  /**
   * TAG is defined for logging errors and debugging information
   */
  private val TAG = AvatarData::class.java.simpleName

  /**
   * Get an avatar data into an image
   * @return Uri if found. Otherwise returns null
   */
  @Deprecated("Use setAvatarImage(Context, ImageView) for local copy optimization or getLocalAvatarUri(Context) to deal with a local copy yourself",
      replaceWith = ReplaceWith(expression = "AvatarData.setAvatarImage(context, imageView)"))
  override fun getValue() = when (value) {
    null -> FirebaseAuth.getInstance().currentUser?.photoUrl
    else -> value
  }

  /**
   * Check if user has an avatar
   * @param context A context to check for shared preferences
   * @return true if user has an avatar, otherwise returns false
   */
  fun hasAvatar(context: Context): Boolean =
      context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null) != null || FirebaseAuth.getInstance().currentUser?.photoUrl != null

  /**
   * Get local avatar uri if exists in a given context
   * @param context A context to check in shared preferences
   */
  private fun getLocalAvatarUri(context: Context): String? =
      context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null)

  /**
   * Set an avatar data into an image
   * @param context A context to check in shared preferences
   * @param imageView An image to set avatar image and make it visible if found
   */
  fun setAvatarImage(context: Context, imageView: ImageView) {
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

  /**
   * Set an avatar data
   */
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
    Log.i(TAG, "New value is $value")
    super.setValue(value)
  }

  /**
   * Clear an avatar data
   */
  fun clear(context: Context) {
    this.value = null
    context.getSharedPreferences("pref", Context.MODE_PRIVATE).edit().remove("avatar").apply()
  }
}
