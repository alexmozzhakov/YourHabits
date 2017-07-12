package com.doapps.habits.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PasswordTextWatcher(private val inputEmail: EditText) : TextWatcher {

  override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    // ignored
  }

  override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
    // ignored
  }

  override fun afterTextChanged(editable: Editable) {
    if (inputEmail.text.length < 6) {
      inputEmail.error = "Password is too short"
    }
  }
}
