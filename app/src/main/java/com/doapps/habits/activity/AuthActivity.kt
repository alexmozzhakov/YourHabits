package com.doapps.habits.activity

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.doapps.habits.R
import com.doapps.habits.fragments.LoginFragment
import com.doapps.habits.slider.swipeselector.dpToPixel
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
  val callbackManager: CallbackManager = CallbackManager.Factory.create()
  private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
  private lateinit var mAuth: FirebaseAuth

  public override fun onStart() {
    super.onStart()
    mAuth.addAuthStateListener(mAuthListener)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)
    onConfigurationChanged(resources.configuration)

    mAuth = FirebaseAuth.getInstance()
    mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      if (user != null && !user.isAnonymous)
        startActivity(Intent(this, MainActivity::class.java)) // User is signed in
    }

    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        .add(R.id.frame_layout, LoginFragment())
        .commit()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  public override fun onStop() {
    mAuth.removeAuthStateListener(mAuthListener)
    super.onStop()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    val topImageText = findViewById<View>(R.id.top_image_text)
    // Checks the orientation of the screen
    when (newConfig.orientation) {
      ORIENTATION_PORTRAIT -> {
        topImageText.visibility = View.VISIBLE
        setTopMargin(170f)
      }
      ORIENTATION_LANDSCAPE -> {
        topImageText.visibility = View.INVISIBLE
        setTopMargin(0f)
      }
    }
  }

  fun setTopMargin(margin: Float) {
    val layout = findViewById<FrameLayout>(R.id.frame_layout)
    val params = layout.layoutParams as FrameLayout.LayoutParams
    params.topMargin =
        when (margin) {
          0f -> 0
          else -> margin.dpToPixel(applicationContext)
        }
    layout.layoutParams = params
  }

  fun toTerms() =
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://habit.esy.es/terms.txt")))

  override fun onBackPressed() = finishAffinity()
}
