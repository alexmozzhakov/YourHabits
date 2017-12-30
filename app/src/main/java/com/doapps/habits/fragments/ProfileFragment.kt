package com.doapps.habits.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.doapps.habits.BuildConfig
import com.doapps.habits.activity.EditPhotoActivity
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.data.AvatarData
import com.doapps.habits.listeners.ProfileAvatarListener
import com.doapps.habits.listeners.UserRemoveCompleteListener
import com.doapps.habits.slider.swipeselector.PixelUtils
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ProfileFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    activity!!.findViewById<View>(R.id.toolbar_shadow).visibility = View.GONE
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_profile, container, false)
    val toolbar = (activity as MainActivity).toolbar
    toolbar.setTitle(R.string.profile)
    val name: TextView = result.findViewById(R.id.name)
    val email: TextView = result.findViewById(R.id.email)
    val location: TextView = result.findViewById(R.id.location)
    val btnDelete: Button = result.findViewById(R.id.btn_delete_user)
    val fab: View = result.findViewById(R.id.fab)
    val plus: ImageView = result.findViewById(R.id.plus_overlay)

    fab.setOnClickListener {
      childFragmentManager
          .beginTransaction()
          .replace(R.id.user_info, ProfileEditFragment())
          .commit()
      editorOpened[0] = true
    }

    val avatar = result.findViewById<ImageView>(R.id.avatarImage)
    val topPanel: View = result.findViewById(R.id.topPanel)

    if (user != null) {
      location.text = activity!!
          .getSharedPreferences("pref", Context.MODE_PRIVATE)
          .getString("location", "")

      if (AvatarData.hasAvatar(context!!.applicationContext)) {
        AvatarData.getAvatar(context!!.applicationContext, avatar)
      } else {
        if (BuildConfig.DEBUG) Log.i(TAG, "No avatar")
        if (topPanel.height - PixelUtils.dpToPixel(context, 50f) < 200) {
          plus.visibility = View.VISIBLE
        }
        plus.setOnClickListener {
          val intent = Intent(activity, EditPhotoActivity::class.java)
          startActivity(intent)
        }
      }

      // FIXME
      topPanel.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        if (topPanel.height - PixelUtils.dpToPixel(context, 50f) < 200) {
          if (user!!.photoUrl != null) {
            avatar.visibility = View.GONE
          }
          plus.imageAlpha = 0
          Log.i("Top Panel", "I really can't fit on top panel, the view is only " + (topPanel.height - PixelUtils.dpToPixel(context, 50f)))
        } else {
          if (user!!.photoUrl != null) {
            avatar.visibility = View.VISIBLE
            avatar.scaleType = ImageView.ScaleType.FIT_CENTER
          }
          plus.imageAlpha = 255
          Log.i("Top Panel", "I fit on top panel")
        }
      }

      if (!MainActivity.isFacebook(user!!)) {
        val btnFacebook = result.findViewById<Button>(R.id.btn_connect_facebook)
        btnFacebook.visibility = View.VISIBLE
        val callbackManager = (activity as MainActivity).callbackManager
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
              override fun onSuccess(result: LoginResult) {
                if (BuildConfig.DEBUG) {
                  Log.d(TAG, "facebook:onSuccess:" + result)
                }
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                user!!.linkWithCredential(credential)
                    .addOnCompleteListener { task ->
                      if (BuildConfig.DEBUG) {
                        Log.d("FA", "linkWithCredential:onComplete:" + task.isSuccessful)
                      }

                      if (task.isSuccessful) {
                        Toast.makeText(context!!.applicationContext,
                            task.result.toString() + " Successfully connected with Facebook",
                            Toast.LENGTH_SHORT).show()
                      } else {
                        Toast.makeText(context!!.applicationContext,
                            "Authentication failed", Toast.LENGTH_SHORT).show()
                      }
                    }
              }

              override fun onCancel() {
                Log.d(TAG, "Facebook login canceled")
              }

              override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
              }
            })
        // set up facebook btn
        btnFacebook.setOnClickListener {
          LoginManager.getInstance().logInWithReadPermissions(activity!!, Arrays.asList("email", "public_profile"))
        }
      }

      name.text = user!!.displayName
      email.text = user!!.email

      btnDelete.setOnClickListener {
        user!!.delete().addOnCompleteListener(UserRemoveCompleteListener(activity!!))
      }
      AvatarData.observe(this, ProfileAvatarListener(context!!, avatar, activity!!, this, plus))
    }

    return result
  }

  companion object {
    internal val editorOpened = BooleanArray(1)
    private val TAG = ProfileFragment::class.java.simpleName
    var user = FirebaseAuth.getInstance().currentUser
  }
}