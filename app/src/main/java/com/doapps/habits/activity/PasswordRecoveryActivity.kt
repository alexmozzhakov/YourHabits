package com.doapps.habits.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.doapps.habits.R
import com.doapps.habits.helper.EmailTextWatcher
import com.google.firebase.auth.FirebaseAuth

class PasswordRecoveryActivity : AppCompatActivity() {
  private lateinit var emailEdit: TextInputEditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_password_recovery)
    emailEdit = findViewById(R.id.textInputEditText)
  }

  fun recoverPassword() {
    val newEmail = emailEdit.text.toString().trim { it <= ' ' }
    when {
      newEmail.isEmpty() -> Toast.makeText(applicationContext, "Email is empty", Toast.LENGTH_SHORT).show()
      EmailTextWatcher.isValidEmail(newEmail) -> {
        FirebaseAuth.getInstance().sendPasswordResetEmail(newEmail)
        Toast.makeText(applicationContext, "Recovery mail sent", Toast.LENGTH_SHORT).show()
        startActivity(Intent(applicationContext, AuthActivity::class.java))
      }
      else -> Toast.makeText(applicationContext, "Recovery email isn't valid", Toast.LENGTH_SHORT).show()
    }

    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
  }
}
