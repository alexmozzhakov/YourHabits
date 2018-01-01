package com.doapps.habits.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.doapps.habits.BuildConfig
import com.doapps.habits.R
import com.doapps.habits.data.AvatarData
import com.doapps.habits.fragments.*
import com.doapps.habits.helper.HabitListManager
import com.doapps.habits.listeners.MenuAvatarListener
import com.doapps.habits.listeners.NameChangeListener
import com.doapps.habits.models.Habit
import com.doapps.habits.slider.swipeselector.dpToPixel
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  val callbackManager: CallbackManager = CallbackManager.Factory.create()
  var mLastFragment = -1
  private lateinit var mNavigationView: NavigationView
  private var user: FirebaseUser? = null
  private lateinit var mDrawerToggle: ActionBarDrawerToggle
  lateinit var toolbar: Toolbar
    private set
  private var mDrawerLayout: DrawerLayout? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Fabric.with(this, Crashlytics())
    setContentView(R.layout.activity_main)
    user = FirebaseAuth.getInstance().currentUser
    toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    handleIntentAction()

    if (savedInstanceState == null && mLastFragment != R.id.nav_list) {
      supportFragmentManager
          .beginTransaction()
          .add(R.id.content_frame, HomeFragment())
          .commit()
      mLastFragment = R.id.nav_home
    }

    mNavigationView = findViewById(R.id.navigationView)

    if (user == null) {
      FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener { user = FirebaseAuth.getInstance().currentUser }
      anonymousMenuSetup()
    } else {
      onSetupNavigationDrawer(user)
      NameChangeListener.listener.addObserver { _, _ ->
        val navName = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.name_info)
        navName.text = user!!.displayName
      }
    }

    mDrawerLayout = findViewById(R.id.drawer_layout)
    mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
      override fun onDrawerOpened(drawerView: View) {
        if (mLastFragment == R.id.nav_profile) {
          closeImm()
        }
        super.onDrawerOpened(drawerView)
      }
    }

    mDrawerLayout!!.addDrawerListener(mDrawerToggle)

    if (actionBar != null) {
      actionBar!!.setHomeButtonEnabled(true)
    }

    AvatarData.observe(this, MenuAvatarListener(this, applicationContext, mNavigationView))
  }

  private fun handleIntentAction() {
    if (intent.action == "no" || intent.action == "yes") {
      Log.i("MainActivity", "id = ${intent.extras["id"]}")
      val id: Long = intent.getLongExtra("id", 0)
      val habit: Habit = HabitListManager.getInstance(this)[id]
      if (BuildConfig.DEBUG) Log.i("Notification Action", intent.action)
      when (intent.action) {
        "yes" -> habit.isDoneMarker = true
        "no" -> habit.isDoneMarker = false
      }
      HabitListManager.getInstance(this).update(habit)
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.cancel(id.toInt())
      supportFragmentManager
          .beginTransaction()
          .add(R.id.content_frame, ListFragment())
          .commit()
      mLastFragment = R.id.nav_list
      toolbar.title = "List"
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    mDrawerLayout!!.closeDrawer(GravityCompat.START)

    val id = item.itemId

    if (id == R.id.nav_logout) {
      logoutUser()
    } else if (mLastFragment != id) {
      if (mLastFragment == R.id.nav_profile) {
        findViewById<View>(R.id.toolbar_shadow).visibility = View.VISIBLE
      } else if (mLastFragment == R.id.nav_programs) {
        ProgramsFragment.isShowing = false
      }

      Handler().postDelayed({
        val transaction = supportFragmentManager.beginTransaction()
        when (id) {
          R.id.nav_home -> transaction.replace(R.id.content_frame, HomeFragment()).commit()
          R.id.nav_programs -> transaction.replace(R.id.content_frame, ProgramsFragment()).commit()
          R.id.nav_list -> transaction.replace(R.id.content_frame, ListFragment()).commit()
          R.id.nav_profile -> transaction.replace(R.id.content_frame, ProfileFragment()).commit()
        }
        mLastFragment = id
      }, INFLATE_DELAY)
    }

    return true
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState()
  }

  fun setChecked(index: Int) {
    mNavigationView.menu.getItem(index).isChecked = true
  }

  @Suppress("UNUSED_PARAMETER")
  fun toCreateFragment(view: View) {
    mNavigationView.menu.getItem(0).isChecked = false
    mNavigationView.menu.getItem(2).isChecked = false
    mLastFragment = 5

    supportFragmentManager
        .beginTransaction()
        .replace(R.id.content_frame, CreateFragment())
        .commit()
  }

  @Suppress("UNUSED_PARAMETER")
  private fun toProfile(view: View) {
    mDrawerLayout!!.closeDrawer(GravityCompat.START)

    if (mLastFragment == R.id.nav_profile) {
      return
    }
    Handler().postDelayed({
      toolbar.setTitle(R.string.profile)
      mNavigationView.menu.getItem(3).isChecked = true

      findViewById<View>(R.id.toolbar_shadow).visibility = View.GONE
      supportFragmentManager
          .beginTransaction()
          .replace(R.id.content_frame, ProfileFragment())
          .commit()
      mLastFragment = R.id.nav_profile
    }, INFLATE_DELAY)
  }

  private fun anonymousMenuSetup() {
    mNavigationView.setNavigationItemSelectedListener(this)
    mNavigationView.menu.getItem(0).isChecked = true

    if (BuildConfig.DEBUG) Log.i("FirebaseAuth", "User is anonymous")
    mNavigationView.menu.findItem(R.id.nav_logout).isVisible = true
    mNavigationView.menu.findItem(R.id.nav_logout).title = "Login"
    mNavigationView.getHeaderView(0).visibility = View.GONE

  }

  fun removeAvatarPadding() {
    mNavigationView.getHeaderView(0).findViewById<View>(R.id.fields_info).setPadding(0, 0, 0, 0)
  }

  fun onSetupNavigationDrawer(user: FirebaseUser?) {

    mNavigationView.setNavigationItemSelectedListener(this)
    mNavigationView.menu.getItem(0).isChecked = true
    if (user != null && user.isAnonymous) {
      if (BuildConfig.DEBUG) Log.i("FirebaseAuth", "User is anonymous")
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = true
      mNavigationView.menu.findItem(R.id.nav_logout).title = "Login"
      mNavigationView.getHeaderView(0).visibility = View.GONE
    } else if (user != null) {
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = true
      mNavigationView.menu.findItem(R.id.nav_profile).isVisible = true
      if (BuildConfig.DEBUG) Log.i("FirebaseAuth", "Regular user")
      val navName = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.name_info)
      val navEmail = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.email_info)
      navName.text = user.displayName
      navEmail.text = user.email
      mNavigationView.getHeaderView(0).setOnClickListener({ toProfile(it) })
      val avatar = mNavigationView.getHeaderView(0).findViewById<ImageView>(R.id.profile_photo)
      if (AvatarData.hasAvatar(applicationContext)) {
        AvatarData.getAvatar(applicationContext, avatar)
        mNavigationView.getHeaderView(0)
            .findViewById<View>(R.id.fields_info).setPadding(
            72f.dpToPixel(applicationContext), 0, 0, 0)
      }
    } else {
      if (BuildConfig.DEBUG) Log.w("FirebaseAuth", "User is null")
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = false
      mNavigationView.getHeaderView(0).visibility = View.GONE
    }
  }

  private fun logoutUser() {
    // Sign out from account manager
    if (user != null) {
      if (user!!.isAnonymous) {
        user!!.delete()
      } else {
        AvatarData.clear(this)
        FirebaseAuth.getInstance().signOut()
        if (BuildConfig.DEBUG) Log.i("FA", "user was signed out")
      }
    }
    // Launching the login activity
    val intent = Intent(this, AuthActivity::class.java)
    startActivity(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  fun closeImm() {
    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (currentFocus != null) {
      imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    if (BuildConfig.DEBUG) Log.i("IMM", "Closed imm")
  }

  override fun onBackPressed() {
    if (BuildConfig.DEBUG) Log.d("CDA", "onBackPressed Called")
    if (mLastFragment == R.id.nav_home) finishAffinity()
    if (mLastFragment == R.id.nav_profile) findViewById<View>(R.id.toolbar_shadow).visibility = View.VISIBLE

    supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()
    toolbar.setTitle(R.string.home)
    mLastFragment = R.id.nav_home
    mNavigationView.menu.getItem(0).isChecked = true
  }

  override fun onDestroy() {
    super.onDestroy()
    if (user != null && user?.isAnonymous!!) user!!.delete()
  }

  companion object {
    private val INFLATE_DELAY = 200L

    fun isFacebook(user: FirebaseUser): Boolean {
      if (user.providers != null) {
        user.providers!!
            .filter { it.contains("facebook") }
            .forEach { return true }
      }
      return false
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    toolbar.title = when (mLastFragment) {
      R.id.nav_home -> "Home"
      R.id.nav_list -> "List"
      R.id.nav_profile -> "Profile"
      R.id.nav_programs -> "Programs"
      5 -> "Create habit"
      else -> "Home"
    }
  }
}
