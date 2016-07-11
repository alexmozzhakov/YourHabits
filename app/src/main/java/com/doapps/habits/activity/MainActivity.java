package com.doapps.habits.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.fragments.CreateFragment;
import com.doapps.habits.fragments.HomeFragment;
import com.doapps.habits.fragments.ListFragment;
import com.doapps.habits.fragments.ProfileFragment;
import com.doapps.habits.fragments.ProgramsFragment;
import com.doapps.habits.helper.RoundedTransformation;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final long INFLATE_DELAY = 200L;
    // TODO: 16/06/2016 move navigation logic to another class
    private int mLastFragment = -1;
    private NavigationView mNavigationView;
    private FirebaseUser user;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @SuppressWarnings("CommitTransaction")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("Home");
        }

        if (savedInstanceState == null) {
            Log.i("Main", "null == savedInstanceState");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, new HomeFragment())
                    .commit();
            mLastFragment = 0;
        }

        if (user == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task ->
                    onSetupNavigationDrawer());
        } else {
            onSetupNavigationDrawer();
            if (user.getPhotoUrl() != null) {
                Log.i("FA image url", String.valueOf(user.getPhotoUrl()));

            }
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);


        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    @SuppressWarnings("CommitTransaction")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {


        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        new Handler().postDelayed(() -> {
            final int id = item.getItemId();

            if (id == R.id.nav_home && mLastFragment != 0) {
                final FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new HomeFragment()).commit();
                mToolbar.setTitle("Home");
                mLastFragment = 0;
                mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

            } else if (id == R.id.nav_programs && mLastFragment != 1) {
                final FragmentTransaction
                        transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new ProgramsFragment()).commit();
                mToolbar.setTitle("Programs");
                mLastFragment = 1;
                mNavigationView.getMenu().findItem(R.id.nav_programs).setChecked(true);

            } else if (id == R.id.nav_lists && mLastFragment != 2) {
                final FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new ListFragment()).commit();
                mToolbar.setTitle("List");
                mLastFragment = 2;
                mNavigationView.getMenu().findItem(R.id.nav_lists).setChecked(true);
            } else if (id == R.id.nav_profile && mLastFragment != 3) {
                final FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new ProfileFragment()).commit();
                mToolbar.setTitle("Profile");
                mLastFragment = 3;
                mNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            } else if (id == R.id.nav_logout) {
                logoutUser();
            }
            ProgramsFragment.isShowing = false;

        }, INFLATE_DELAY);

        return true;
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void toCreateFragment(final View view) {
        mNavigationView.getMenu().getItem(0).setChecked(false);
        mLastFragment = 4;
        mToolbar.setTitle("Create Habit");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new CreateFragment())
                .commit();

    }

    public void toProfile(final View view) {
        if (mLastFragment == 3 || user.isAnonymous()) {
            return;
        }

        mToolbar.setTitle("Profile");

        if (mLastFragment != -1) {
            mNavigationView.getMenu().getItem(mLastFragment).setChecked(false);
        }
        mNavigationView.getMenu().getItem(3).setChecked(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ProfileFragment())
                .commit();

        mDrawerLayout.closeDrawers();
    }

    private void onSetupNavigationDrawer() {
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            mNavigationView.getMenu().getItem(0).setChecked(true);
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.isAnonymous()) {
                Log.i("FirebaseAuth", "User is anonymous");
                mNavigationView.getMenu().findItem(R.id.nav_logout).setTitle("Login");
            } else if (user != null) {
                mNavigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                Log.i("FirebaseAuth", "Regular user");
                final TextView navName =
                        (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
                final TextView navEmail =
                        (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_info);
                final ImageView avatar =
                        (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_photo);
                navName.setText(user.getDisplayName());
                navEmail.setText(user.getEmail());
                Picasso.with(getApplicationContext())
                        .load(user.getPhotoUrl())
                        .transform(new RoundedTransformation())
                        .into(avatar);
                avatar.setVisibility(View.VISIBLE);
            } else {
                Log.w("FirebaseAuth", "User is null");
                mNavigationView.getMenu().findItem(R.id.nav_logout).setTitle("Login");
            }
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private void logoutUser() {
        // Sign out from account manager
        if (user.isAnonymous()) {
            user.delete();
        } else {
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();

            FirebaseAuth.getInstance().signOut();
            Log.i("FA", "user was signed out");
        }

        // Launching the login activity
        final Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

}
