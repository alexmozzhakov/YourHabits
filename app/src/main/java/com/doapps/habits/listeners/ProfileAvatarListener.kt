package com.doapps.habits.listeners

import android.app.Activity
import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.doapps.habits.activity.EditPhotoActivity
import com.doapps.habits.helper.PicassoRoundedTransformation
import com.squareup.picasso.Picasso
import java.io.File


class ProfileAvatarListener(private val context: Context, private val avatar: ImageView,
                            private val activity: Activity, lifecycleOwner: LifecycleOwner,
                            private val plus: ImageView) : LifecycleObserver, Observer<Uri> {
  private val lifecycle: Lifecycle = lifecycleOwner.lifecycle


  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  internal fun cleanup() = lifecycle.removeObserver(this)

  override fun onChanged(uri: Uri?) {
    val localAvatarUri =
        context.applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("avatar", null)

    val optimalFile = if (localAvatarUri != null) File(localAvatarUri) else null

    if (uri != null) {
      Log.i(TAG, "Avatar update detected")

      if (optimalFile == null) {
        Picasso.with(context.applicationContext).invalidate(uri)
        Picasso.with(context.applicationContext)
            .load(uri)
            .transform(PicassoRoundedTransformation())
            .fit().centerInside()
            .into(avatar)
      } else {
        Picasso.with(context.applicationContext)
            .load(optimalFile)
            .transform(PicassoRoundedTransformation())
            .fit().centerInside()
            .into(avatar)

      }
      avatar.invalidate()
      plus.visibility = View.INVISIBLE
    } else {
      Log.w(TAG, "no avatar")
      plus.visibility = View.VISIBLE
      plus.setOnClickListener {
        activity.startActivity(Intent(activity, EditPhotoActivity::class.java))
      }
    }

  }

  companion object {
    val TAG: String = ProfileAvatarListener::class.java.simpleName
  }
}
