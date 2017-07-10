package com.doapps.habits.activity

import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.doapps.habits.R
import com.doapps.habits.data.AvatarData
import com.doapps.habits.fragments.*
import com.doapps.habits.helper.PicassoRoundedTransformation
import com.doapps.habits.listeners.MenuAvatarListener
import com.doapps.habits.listeners.NameChangeListener
import com.doapps.habits.slider.swipeselector.PixelUtils
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class MainActivity : LifecycleActivity(), NavigationView.OnNavigationItemSelectedListener {
  val callbackManager: CallbackManager = CallbackManager.Factory.create()
  private var mLastFragment = -1
  private lateinit var mNavigationView: NavigationView
  private var user: FirebaseUser? = null
  private lateinit var mDrawerToggle: ActionBarDrawerToggle
  lateinit var toolbar: Toolbar
    private set
  private var mDrawerLayout: DrawerLayout? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    user = FirebaseAuth.getInstance().currentUser
    toolbar = findViewById<Toolbar>(R.id.toolbar)

    //        if (getIntent().getAction() != null) {
    //            long id = getIntent().getLongExtra("id", 0);
    //            Habit habit = HabitListManager.getInstance(this).getDatabase().habitDao().get(id);
    //            if (getIntent().getAction().equals("no")) {
    //                Log.i("Action", "No");
    //                habit.setDoneMarker(false);
    //                HabitListManager.getInstance(this).getDatabase().habitDao().update(habit);
    //                getSupportFragmentManager()
    //                        .beginTransaction()
    //                        .add(R.id.content_frame, new ListFragment())
    //                        .commit();
    //                mLastFragment = R.id.nav_lists;
    //            } else if (getIntent().getAction().equals("yes")) {
    //                Log.i("Action", "Yes");
    //                habit.setDoneMarker(true);
    //                HabitListManager.getInstance(this).getDatabase().habitDao().update(habit);
    //                getSupportFragmentManager()
    //                        .beginTransaction()
    //                        .add(R.id.content_frame, new ListFragment())
    //                        .commit();
    //                mLastFragment = R.id.nav_lists;
    //            }
    //            mToolbar.setTitle("List");
    //            NotificationManager notificationManager = (NotificationManager)
    //                    getSystemService(Context.NOTIFICATION_SERVICE);
    //            notificationManager.cancel((int) id);
    //        }

    if (savedInstanceState == null && mLastFragment != R.id.nav_lists) {
      supportFragmentManager
          .beginTransaction()
          .add(R.id.content_frame, HomeFragment())
          .commit()
      mLastFragment = R.id.nav_home
      toolbar.title = "Home"
    }

    mNavigationView = findViewById<NavigationView>(R.id.navigationView)

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

    mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
    mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
      override fun onDrawerOpened(drawerView: View?) {
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

    AvatarData.getInstance().observe(this,
        MenuAvatarListener(this, applicationContext, mNavigationView))
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
        if (id == R.id.nav_home) {
          transaction.replace(R.id.content_frame, HomeFragment()).commit()
        } else if (id == R.id.nav_programs) {
          transaction.replace(R.id.content_frame, ProgramsFragment()).commit()
        } else if (id == R.id.nav_lists) {
          transaction.replace(R.id.content_frame, ListFragment()).commit()
        } else if (id == R.id.nav_profile) {
          transaction.replace(R.id.content_frame, ProfileFragment()).commit()
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

  fun toCreateFragment(view: View) {
    mNavigationView.menu.getItem(0).isChecked = false
    mNavigationView.menu.getItem(2).isChecked = false
    mLastFragment = 5

    supportFragmentManager
        .beginTransaction()
        .replace(R.id.content_frame, CreateFragment())
        .commit()

  }

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

  fun anonymousMenuSetup() {
    mNavigationView.setNavigationItemSelectedListener(this)
    mNavigationView.menu.getItem(0).isChecked = true

    Log.i("FirebaseAuth", "User is anonymous")
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
      Log.i("FirebaseAuth", "User is anonymous")
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = true
      mNavigationView.menu.findItem(R.id.nav_logout).title = "Login"
      mNavigationView.getHeaderView(0).visibility = View.GONE
    } else if (user != null) {
      mNavigationView.menu.findItem(R.id.nav_logout).isVisible = true
      mNavigationView.menu.findItem(R.id.nav_profile).isVisible = true
      Log.i("FirebaseAuth", "Regular user")
      val navName = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.name_info)
      val navEmail = mNavigationView.getHeaderView(0).findViewById<TextView>(R.id.email_info)
      navName.text = user.displayName
      navEmail.text = user.email
      mNavigationView.getHeaderView(0).setOnClickListener({ this.toProfile(it) })
      if (user.photoUrl != null) {
        mNavigationView.getHeaderView(0)
            .findViewById<View>(R.id.fields_info).setPadding(
            PixelUtils.dpToPixel(this, 72f).toInt(), 0, 0, 0)
        val avatar = mNavigationView.getHeaderView(0).findViewById<ImageView>(R.id.profile_photo)

        Log.i("updateMenuAvatar", user.photoUrl.toString())
        Picasso.with(applicationContext)
            .load(user.photoUrl)
            .transform(PicassoRoundedTransformation())
            .into(avatar)
        avatar.visibility = View.VISIBLE
      }
    } else {
      Log.w("FirebaseAuth", "User is null")
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
        FirebaseAuth.getInstance().signOut()
        Log.i("FA", "user was signed out")
      }
    }
    // Launching the login activity
    val intent = Intent(this, AuthActivity::class.java)
    startActivity(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    callbackManager.onActivityResult(requestCode, resultCode, data)
  }

  fun closeImm() {
    val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (currentFocus != null) {
      imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    Log.i("IMM", "Closed imm")
  }

  override fun onBackPressed() {
    Log.d("CDA", "onBackPressed Called")
    if (mLastFragment == R.id.nav_home)
      finishAffinity()

    if (mLastFragment == R.id.nav_profile) {
      findViewById<View>(R.id.toolbar_shadow).visibility = View.VISIBLE
    }
    supportFragmentManager.beginTransaction()
        .replace(R.id.content_frame, HomeFragment()).commit()
    toolbar.setTitle(R.string.home)
    mLastFragment = R.id.nav_home
    mNavigationView.menu.getItem(0).isChecked = true
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
}
