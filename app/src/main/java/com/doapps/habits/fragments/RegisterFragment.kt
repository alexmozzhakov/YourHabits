package com.doapps.habits.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.doapps.habits.R
import com.doapps.habits.helper.EmailTextWatcher
import com.doapps.habits.helper.PasswordTextWatcher
import com.doapps.habits.listeners.NameChangeListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

  private fun handleRegisterError(task: Task<*>, activity: Activity?) {
    task.addOnFailureListener(activity!!
    ) { fail ->
      // Sign in failed.
      Log.e("task failed", fail.message)
      Toast.makeText(
          activity.applicationContext,
          fail.message,
          Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_register, container, false)
    val inputFullName = result.findViewById<EditText>(R.id.full_name)
    val inputEmail = result.findViewById<EditText>(R.id.email)
    val inputPassword = result.findViewById<EditText>(R.id.password)
    inputPassword.addTextChangedListener(PasswordTextWatcher(inputPassword))
    val btnRegister = result.findViewById<Button>(R.id.btnCreate)
    val btnLinkToLogin = result.findViewById<Button>(R.id.btnLinkToLoginScreen)

    btnLinkToLogin.setOnClickListener { toLoginActivity() }
    inputEmail.setText(
        activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getString("last email", ""))
    inputEmail.addTextChangedListener(EmailTextWatcher(inputEmail))

    val mAuth = FirebaseAuth.getInstance()
    // Register Button Click event
    btnRegister.setOnClickListener {
      val name = inputFullName.text.toString().trim { it <= ' ' }
      val email = inputEmail.text.toString().trim { it <= ' ' }
      val password = inputPassword.text.toString().trim { it <= ' ' }

      if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity!!) { task ->
              if (task.isSuccessful) {
                setUserName(name)
                toLoginActivity()
              } else {
                handleRegisterError(task, activity)
              }
            }
      } else {
        Toast.makeText(activity!!.applicationContext,
            "Please enter your details!", Toast.LENGTH_LONG)
            .show()
      }
    }

    return result
  }

  private fun toLoginActivity() {
    activity!!.supportFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        .replace(R.id.frame_layout, LoginFragment()).commit()
  }

  private fun setUserName(name: String) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
      val changeRequest = UserProfileChangeRequest.Builder()
      changeRequest.setDisplayName(name)
      user.updateProfile(changeRequest.build()).addOnCompleteListener {
        if (NameChangeListener.listener.countObservers() == 0) {
          if (activity != null) {
            val nav = activity!!.findViewById<NavigationView>(R.id.navigationView)
            if (nav != null) {
              (nav.getHeaderView(0).findViewById<View>(R.id.name_info) as TextView).text = name
            } else {
              Log.i("updateProfile", "nav is null")
            }
          } else {
            Log.i("updateProfile", "activity is null")
          }
        } else {
          NameChangeListener.listener.setChanged()
          Log.i("NameChangeListener", "notifyObservers")
        }
      }
    }
  }

}
