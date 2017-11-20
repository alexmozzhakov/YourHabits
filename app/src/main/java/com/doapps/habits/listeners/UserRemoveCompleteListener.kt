package com.doapps.habits.listeners

import android.app.AlertDialog
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.doapps.habits.BuildConfig
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.data.AvatarData
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class UserRemoveCompleteListener(private val activity: FragmentActivity) : OnCompleteListener<Void> {

  private var mDialogTextLength: Int = 0
  private val dialogMinLength = 6
  private val user = FirebaseAuth.getInstance().currentUser!!

  override fun onComplete(task: Task<Void>) = when {
    task.isSuccessful -> onSuccess()
    task.exception is FirebaseException -> onReAuthNeeded()
    else -> Toast.makeText(activity.applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
  }

  private fun onReAuthNeeded() {
    if (MainActivity.isFacebook(user)) {
      LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"))
      val callbackManager = (activity as MainActivity).callbackManager
      LoginManager.getInstance().registerCallback(callbackManager,
          object : FacebookCallback<LoginResult> {
            private val TAG: String = "FacebookCallback"

            override fun onSuccess(result: LoginResult) {
              if (BuildConfig.DEBUG) Log.d(TAG, "facebook:onSuccess:" + result)
              val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
              user.reauthenticate(credential).addOnCompleteListener {
                user.delete().addOnCompleteListener(this@UserRemoveCompleteListener)
              }
            }

            override fun onCancel() {
              if (BuildConfig.DEBUG) Log.d(TAG, "Facebook login canceled")
            }

            override fun onError(error: FacebookException) {
              if (BuildConfig.DEBUG) Log.d(TAG, "facebook:onError", error)
            }
          })
    } else {
      val builder = AlertDialog.Builder(activity)
      builder.setTitle("Please re-enter your password")
      val input = EditText(activity.applicationContext)

      input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
      builder.setView(input)

      // Set up the buttons
      builder.setPositiveButton("OK") { _, _ ->
        val inputPassword = input.text.toString()
        if (!inputPassword.isEmpty() && user.email != null) {
          val credential = EmailAuthProvider.getCredential(user.email!!, inputPassword)
          user.reauthenticate(credential).addOnCompleteListener { user.delete().addOnCompleteListener(this) }
        } else if (inputPassword.isEmpty()) {
          Toast.makeText(activity.applicationContext, "Password was empty",
              Toast.LENGTH_SHORT).show()
        }
      }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
      val dialog = builder.create()
      dialog.show()

      input.setOnKeyListener { _, _, _ ->
        mDialogTextLength++
        if (mDialogTextLength > dialogMinLength) {
          dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        }
        false
      }
    }
  }

  private fun onSuccess() {
    if (BuildConfig.DEBUG) Log.d("FA", "User account deleted.")
    AvatarData.clear(activity)
    activity.startActivity(Intent(activity, MainActivity::class.java))
  }
}
