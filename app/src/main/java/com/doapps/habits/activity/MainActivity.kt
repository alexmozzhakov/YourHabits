package com.doapps.habits.activity

import android.annotation.SuppressLint
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
import android.widget.Toast
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.fabric.sdk.android.Fabric

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  /**
   * The callback manager for Facebook
   */
  val callbackManager: CallbackManager = CallbackManager.Factory.create()
  /**
   * An [Integer] to save last fragment number
   * -1 by default
   */
  var mLastFragment = -1
  /**
   * A listener of NavigationDrawer opening
   */
  private lateinit var mDrawerToggle: ActionBarDrawerToggle
  /**
   * [NavigationView] view
   */
  private lateinit var mNavigationView: NavigationView
  /**
   * User's avatar [ImageView]
   */
  private lateinit var avatar: ImageView
  /**
   * A toolbar of an [AppCompatActivity]
   */
  lateinit var toolbar: Toolbar
    private set
  /**
   * [FirebaseUser] user object
   */
  private var user: FirebaseUser? = null
  /**
   * A layout of a drawer
   */
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
    avatar = mNavigationView.getHeaderView(0).findViewById(R.id.profile_photo)

    if (user == null) {
      if (isGooglePlayServicesAvailable(this))
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
        if (mLastFragment == R.id.nav_profile) closeImm()
        super.onDrawerOpened(drawerView)
      }
    }

    mDrawerLayout!!.addDrawerListener(mDrawerToggle)

    if (actionBar != null) actionBar.setHomeButtonEnabled(true)

    AvatarData.observe(this, MenuAvatarListener(this, applicationContext, mNavigationView))
  }

  /**
   * Handles notification response if supplied with intent
   */
  private fun handleIntentAction() {
    if (intent.action == "no" || intent.action == "yes") {
      val id: Int = intent.getIntExtra("id", 0)
      val habit: Habit = HabitListManager.getInstance(this)[id.toLong()]!!
      if (BuildConfig.DEBUG) Log.i(TAG, "${intent.action} for id = $id")
      when (intent.action) {
        "yes" -> habit.isDoneMarker = true
        "no" -> habit.isDoneMarker = false
      }
      HabitListManager.getInstance(this).update(habit)
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.cancel(id)
      mLastFragment = R.id.nav_list
      toolbar.title = getString(R.string.list)
      supportFragmentManager
          .beginTransaction()
          .add(R.id.content_frame, ListFragment())
          .commit()
    }
  }

  /**
   * Checks for GooglePlayServices
   */
  private fun isGooglePlayServicesAvailable(context: Context): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    return resultCode == ConnectionResult.SUCCESS
  }

  /**
   * Handles navigation item selection
   */
  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    mDrawerLayout!!.closeDrawer(GravityCompat.START)

    val id = item.itemId

    if (id == R.id.nav_logout) {
      logoutUser()
      startActivity(Intent(this, AuthActivity::class.java))
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

  /**
   * Syncs the toggle state after onRestoreInstanceState has occurred.
   */
  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    mDrawerToggle.syncState()
  }

  /**
   * Sets an navigation item of the Activity checked by index
   * @param index an index to be set as checked
   */
  fun setChecked(index: Int) {
    mNavigationView.menu.getItem(index).isChecked = true
  }


  /**
   * Handles transition to CreateFragment
   */
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

  /**
   * Handles transition to profile from navigation drawer
   */
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

  /**
   * Sets up menu for anonymous user
   */
  private fun anonymousMenuSetup() {
    mNavigationView.setNavigationItemSelectedListener(this)
    mNavigationView.menu.getItem(0).isChecked = true

    if (BuildConfig.DEBUG) Log.i("FirebaseAuth", "User is anonymous")
    if (isGooglePlayServicesAvailable(this)) {
      mNavigationView.menu.findItem(R.id.nav_logout).title = "Login"
    } else {
      Toast.makeText(applicationContext, "Please enable Google Play Services for login to work", Toast.LENGTH_SHORT).show()
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = false
    }
    mNavigationView.getHeaderView(0).visibility = View.GONE

  }

  /**
   * Removes padding from user's avatar in navigation drawer
   */
  fun removeAvatarPadding() {
    mNavigationView.getHeaderView(0).findViewById<View>(R.id.fields_info).setPadding(0, 0, 0, 0)
  }

  /**
   * Handles start up of the navigation drawer
   */
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
      if (AvatarData.hasAvatar(applicationContext)) {
        AvatarData.setAvatarImage(applicationContext, avatar)
        mNavigationView.getHeaderView(0)
            .findViewById<View>(R.id.fields_info).setPadding(
                72f.dpToPixel(), 0, 0, 0)
      }
    } else {
      if (BuildConfig.DEBUG) Log.w("FirebaseAuth", "User is null")
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = false
      mNavigationView.getHeaderView(0).visibility = View.GONE
    }
  }

  /**
   * Checks whether the current avatar view value is empty
   */
  fun avatarEmpty(): Boolean = avatar.drawable == null

  /**
   * Logs out user and removes user if he/she was anonymous.
   */
  private fun logoutUser() {
    if (user != null) {
      if (user!!.isAnonymous) {
        user!!.delete()
      } else {
        AvatarData.clear(this)
        FirebaseAuth.getInstance().signOut()
        if (BuildConfig.DEBUG) Log.i("FA", "user was signed out")
      }
    }
  }

  /**
   * Sends an activity result to a callback manager
   */
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  /**
   * Handles closing of an application keyboard
   */
  fun closeImm() {
    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (currentFocus != null) {
      imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    if (BuildConfig.DEBUG) Log.i("IMM", "Closed imm")
  }

  /**
   * Handles pressing of back button
   */
  override fun onBackPressed() {
    if (BuildConfig.DEBUG) Log.d("CDA", "onBackPressed Called")
    if (mLastFragment == R.id.nav_home) finishAffinity()
    if (mLastFragment == R.id.nav_profile) findViewById<View>(R.id.toolbar_shadow).visibility = View.VISIBLE

    supportFragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment()).commit()
    toolbar.setTitle(R.string.home)
    mLastFragment = R.id.nav_home
    mNavigationView.menu.getItem(0).isChecked = true
  }

  /**
   * Destroys an [AppCompatActivity] and removes user if it was anonymous
   */
  override fun onDestroy() {
    super.onDestroy()
    if (user != null && user?.isAnonymous!!) user!!.delete()
  }

  companion object {
    /**
     * A delay to let a navigation drawer close and not freeze
     */
    private const val INFLATE_DELAY = 200L

    /**
     * Checks whether given Firebase user is connected with Facebook
     * @param user [FirebaseUser] item to be checked
     */
    @SuppressLint("RestrictedApi")
    fun isFacebook(user: FirebaseUser) = user.providers!!.any { it.contains("facebook") }

    /**
     * TAG is defined for logging errors and debugging information
     */
    private val TAG = MainActivity::class.java.simpleName
  }

  /**
   * Handles device rotation
   */
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
