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


class ProfileAvatarListener(private val context: Context, private val avatar: ImageView,
                            private val activity: Activity, lifecycleOwner: LifecycleOwner,
                            private val plus: ImageView) : LifecycleObserver, Observer<Uri> {
  private val lifecycle: Lifecycle = lifecycleOwner.lifecycle


  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  internal fun cleanup() = lifecycle.removeObserver(this)

  override fun onChanged(uri: Uri?) {
    if (uri != null) {
      Log.i(TAG, "Avatar update detected")
      Picasso.with(context.applicationContext)
          .invalidate(uri)
      Picasso.with(context.applicationContext)
          .load(uri)
          .transform(PicassoRoundedTransformation())
          .fit().centerInside()
          .into(avatar)
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
