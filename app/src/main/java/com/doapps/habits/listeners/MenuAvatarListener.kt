package com.doapps.habits.listeners

import android.arch.lifecycle.*
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.design.widget.NavigationView
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.helper.PicassoRoundedTransformation
import com.doapps.habits.slider.swipeselector.dpToPixel
import com.squareup.picasso.Picasso

class MenuAvatarListener(lifecycleOwner: LifecycleOwner, private val context: Context,
                         private val navigationView: NavigationView)
  : LifecycleObserver, Observer<Uri> {

  private val lifecycle: Lifecycle =
      lifecycleOwner.lifecycle.apply { addObserver(this@MenuAvatarListener) }


  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  internal fun cleanup() = lifecycle.removeObserver(this)

  override fun onChanged(uri: Uri?) {
    if (uri != null) {
      val avatar = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.profile_photo)
      if (BuildConfig.DEBUG) {
        Log.i("updateMenuAvatar", uri.toString())
      }
      Picasso.with(context.applicationContext).invalidate(uri)
      Picasso.with(context.applicationContext)
          .load(uri)
          .transform(PicassoRoundedTransformation())
          .into(avatar)
      avatar.invalidate()

      val padding = 68f.dpToPixel(context)
      val fieldsInfo = navigationView.getHeaderView(0).findViewById<View>(R.id.fields_info)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        fieldsInfo.setPaddingRelative(padding, 0, 0, 0)
      } else {
        fieldsInfo.setPadding(padding, 0, 0, 0)
      }
    } else {
      Log.e(TAG, "URI is null")
    }
  }

  companion object {
    val TAG: String = MenuAvatarListener::class.java.simpleName
  }
}
