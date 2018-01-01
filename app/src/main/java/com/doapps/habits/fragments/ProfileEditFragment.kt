package com.doapps.habits.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.activity.EditPhotoActivity
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.data.AvatarData
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*

class ProfileEditFragment : Fragment() {
  private var inputPassword = ""
  private var fab: FloatingActionButton? = null
  private lateinit var plus: ImageView
  private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_edit_profile, container, false)
    fab = activity!!.findViewById(R.id.fab)
    plus = activity!!.findViewById(R.id.plus_overlay)
    if (plus.imageAlpha == 255) setUpPhotoEdition(plus, activity, this)

    fab!!.setImageResource(R.drawable.ic_check_white_24dp)
    fab!!.setOnClickListener {
      if (user != null && user.email != null) {
        val email = (result.findViewById<View>(R.id.edit_email) as TextView).text.toString().toLowerCase()
        val name = (result.findViewById<View>(R.id.edit_name) as TextView).text.toString()

        if (!isUpdated(user, email, name)) { // same info
          fragmentManager!!.beginTransaction().remove(this).commit()
          Log.i(TAG, "Nothing to update")
          return@setOnClickListener
        }
        if (MainActivity.isFacebook(user)) {
          val callbackManager = (activity as MainActivity).callbackManager
          LoginManager.getInstance().registerCallback(callbackManager,
              object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                  if (BuildConfig.DEBUG) {
                    Log.d(TAG, "facebook:onSuccess:" + result)
                  }
                  val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                  user.reauthenticate(credential).addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }
                  updateUser(activity, user, name, email)
                }

                override fun onCancel() {
                  Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                  Log.d(TAG, "facebook:onError", error)
                }
              })

          fragmentManager!!.beginTransaction().remove(this).commit()
        } else {
          getPasswordFromUser()
          // Prompt the user to re-provide their sign-in credentials
          val credential = EmailAuthProvider
              .getCredential(user.email!!, inputPassword)
          user.reauthenticate(credential).addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }
          if (inputPassword.isEmpty()) { // entered password empty
            Toast.makeText(context!!.applicationContext,
                "Please enter correct password", Toast.LENGTH_SHORT)
                .show()
            getPasswordFromUser()
            return@setOnClickListener
          }
        }

        updateUser(activity, user, name, email)
        fragmentManager!!.beginTransaction().remove(this).commit()
      } else {
        Log.e(TAG, "User is null")
      }
    }

    return result
  }

  private fun getPasswordFromUser() {
    val builder = AlertDialog.Builder(activity!!)
    builder.setTitle("Please re-enter your password")

    // Set up the input
    val input = EditText(activity)
    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    builder.setView(input)

    // Set up the buttons
    builder.setPositiveButton("OK") { _, _ -> inputPassword = input.text.toString() }
    builder.setNegativeButton("Cancel") { _, _ -> fragmentManager!!.beginTransaction().remove(this).commit() }

    builder.show()
  }

  override fun onPause() {
    if (AvatarData.hasAvatar(context!!)) {
      plus.setOnClickListener(null)
      plus.visibility = View.GONE
    }
    // Set up fab back
    fab!!.setImageResource(R.drawable.ic_edit_white_24dp)
    fab!!.setOnClickListener {
      if (!ProfileFragment.editorOpened[0]) {
        parentFragment!!.childFragmentManager
            .beginTransaction()
            .replace(R.id.user_info, ProfileEditFragment())
            .commit()
        ProfileFragment.editorOpened[0] = true
      }
    }

    // Sets editors state to close
    ProfileFragment.editorOpened[0] = false
    // Close keyboard
    (activity as MainActivity).closeImm()
    super.onPause()
  }

  companion object {

    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = ProfileEditFragment::class.java.simpleName

    private fun isUpdated(user: UserInfo, email: String, name: String): Boolean {
      val nameUpdated = !name.isEmpty() && name != user.displayName
      val emailUpdated = !email.isEmpty() && email != user.email
      return nameUpdated || emailUpdated
    }

    private fun updateUser(activity: Activity?, user: FirebaseUser?,
                           name: String, email: String) {
      val nameView = activity!!.findViewById<TextView>(R.id.name)
      val emailView = activity.findViewById<TextView>(R.id.email)

      if (!name.isEmpty() && name != user!!.displayName) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
              if (task.isSuccessful) {
                val navigationView = activity.findViewById<NavigationView>(R.id.navigationView)

                (navigationView
                    .getHeaderView(0).findViewById<View>(R.id.name_info) as TextView).text = user.displayName

                nameView.text = user.displayName

                Log.d(TAG, "User profile updated.")
              }
            }
      }
      if (!email.isEmpty() && email != user!!.email) {
        user.updateEmail(email)
            .addOnCompleteListener { task ->
              if (task.isSuccessful) {
                val navigationView = activity.findViewById<NavigationView>(R.id.navigationView)

                (navigationView
                    .getHeaderView(0).findViewById<View>(R.id.email_info) as TextView).text = user.email

                emailView.text = user.email

                Log.d(TAG, "User email address updated.")
              }
            }
      }

    }

    private fun setUpPhotoEdition(plus: ImageView, activity: Activity?, fragment: Fragment) {
      plus.visibility = View.VISIBLE
      plus.setOnClickListener {
        fragment.parentFragment!!.childFragmentManager.beginTransaction()
            .remove(fragment).commit()
        val intent = Intent(activity, EditPhotoActivity::class.java)
        activity!!.startActivity(intent)
      }
    }
  }
}
