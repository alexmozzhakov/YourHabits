package com.doapps.habits.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.doapps.habits.R
import com.doapps.habits.activity.AuthActivity
import com.doapps.habits.activity.MainActivity
import com.doapps.habits.activity.PasswordRecoveryActivity
import com.doapps.habits.data.AvatarData
import com.doapps.habits.helper.EmailTextWatcher
import com.doapps.habits.helper.PasswordTextWatcher
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginFragment : Fragment() {

  private var mAuth: FirebaseAuth? = null
  private var inputPassword: TextInputEditText? = null
  private var inputEmail: TextInputEditText? = null
  private var btnFacebook: Button? = null
  private var btnLogin: Button? = null
  private var btnRecovery: Button? = null
  private var btnLinkToRegister: Button? = null
  private var btnSkip: Button? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    val result = inflater.inflate(R.layout.fragment_login, container, false)

    inputEmail = result.findViewById(R.id.email)
    inputPassword = result.findViewById(R.id.password)
    setupFields()

    btnLogin = result.findViewById(R.id.btn_login)
    btnSkip = result.findViewById(R.id.btn_skip)
    btnFacebook = result.findViewById(R.id.btn_login_facebook)
    btnRecovery = result.findViewById(R.id.btn_recovery)
    btnLinkToRegister = result.findViewById(R.id.btnLinkToRegisterScreen)

    setupButtons()

    return result
  }

  private fun setupButtons() {
    btnSkip!!.setOnClickListener({ this.anonymousLogin(it) })
    mAuth = FirebaseAuth.getInstance()

    // Login button Click Event
    btnLogin!!.setOnClickListener {
      val email = if (inputEmail != null) inputEmail!!.text.toString().trim { it <= ' ' } else null
      val password = if (inputPassword != null) inputPassword!!.text.toString().trim { it <= ' ' } else null

      // Check for empty data in the form
      checkInput(email, password)
    }

    // Link to Register Screen
    btnLinkToRegister!!.setOnClickListener {
      activity!!.supportFragmentManager.beginTransaction()
          .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
          .replace(R.id.frame_layout, RegisterFragment()).commit()
    }

    btnRecovery!!.setOnClickListener {
      val intent = Intent(activity!!.applicationContext,
          PasswordRecoveryActivity::class.java)
      startActivity(intent)
    }

    val callbackManager = (activity as AuthActivity).callbackManager

    LoginManager.getInstance().registerCallback(callbackManager,
        object : FacebookCallback<LoginResult> {
          override fun onSuccess(result: LoginResult) {
            handleFacebookAccessToken(result.accessToken)
          }

          override fun onCancel() {
            Log.d(TAG, "facebook:onCancel")
          }

          override fun onError(error: FacebookException) {
            Log.d(TAG, "facebook:onError", error)
          }

          private fun handleFacebookAccessToken(token: AccessToken) {
            val credential = FacebookAuthProvider.getCredential(token.token)
            mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.photoUrl != null && user.photoUrl!!.toString()
                        .contains("fbcdn.net")) {

                      AvatarData.setValue(
                          Uri.parse(
                              String.format("https://graph.facebook.com/%s/picture?type=large",
                                  token.userId)))
                    }
                  } else {
                    Log.w(TAG, "signInWithCredential", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                  }
                }
          }

        })
    btnFacebook!!.setOnClickListener {
      LoginManager.getInstance().logInWithReadPermissions(activity!!,
          Arrays.asList("email", "public_profile"))
    }
  }

  private fun checkInput(email: String?, password: String?) {
    // Check for empty data in the form
    if (email!!.isEmpty() || password!!.isEmpty()) {
      // Prompt user to enter credentials
      Toast.makeText(context!!.applicationContext,
          "Please enter the credentials!", Toast.LENGTH_LONG)
          .show()
    } else if (!EmailTextWatcher.isValidEmail(email)) {
      // Prompt user to enter valid credentials
      Toast.makeText(context!!.applicationContext,
          "Please enter valid credentials!", Toast.LENGTH_LONG)
          .show()
    } else {
      // login user
      val auth = FirebaseAuth.getInstance()
      auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (!task.isSuccessful) {
          Toast.makeText(activity!!.applicationContext,
              "Email or password isn't correct",
              Toast.LENGTH_SHORT).show()

          activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
              .edit().putString("last email", email).apply()
        }
      }
    }// Check for valid email
  }

  private fun setupFields() {
    inputEmail!!.addTextChangedListener(EmailTextWatcher(inputEmail!!))
    inputPassword!!.addTextChangedListener(PasswordTextWatcher(inputPassword!!))

    inputPassword!!.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER)
    inputPassword!!.setOnKeyListener { _: View, keyCode: Int, _: KeyEvent ->
      // If the event is a key-down event on the "enter" button
      if (keyCode == KeyEvent.KEYCODE_ENTER) {
        val email = inputEmail!!.text.toString().trim { it <= ' ' }
        val password = inputPassword!!.text.toString().trim { it <= ' ' }

        checkInput(email, password)
        return@setOnKeyListener true
      }
      false
    }
  }

  @Suppress("UNUSED_PARAMETER")
  private fun anonymousLogin(view: View) {
    val intent = Intent(activity, MainActivity::class.java)
    startActivity(intent)
  }

  companion object {

    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = LoginFragment::class.java.simpleName
  }
}
