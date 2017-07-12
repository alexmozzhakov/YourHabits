package com.doapps.habits.helper

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText

class EmailTextWatcher(private val inputEmail: EditText) : TextWatcher {

  override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    // ignored
  }

  override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    // ignored
  }

  override fun afterTextChanged(editable: Editable) {
    if (!Companion.isValidEmail(inputEmail.text)) {
      inputEmail.error = "Invalid Email Address"
    }
  }

  companion object {
    fun isValidEmail(sequence: CharSequence) = Patterns.EMAIL_ADDRESS.matcher(sequence).matches()
  }
}
