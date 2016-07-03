package com.dohabit.activity;


import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.dohabit.R;
import com.dohabit.fragments.CreateFragment;
import com.dohabit.fragments.HomeFragment;
import com.dohabit.fragments.ListFragment;
import com.dohabit.fragments.ProfileFragment;
import com.dohabit.fragments.ProgramsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.dohabit.R.id.nav_profile;

public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    // TODO: 16/06/2016 move navigation logic to another class
    private int mLastFragment = -1;
    private NavigationView mNavigationView;

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar mToolbar;
    private FirebaseUser user;
    private DrawerLayout mDrawerLayout;
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

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, new HomeFragment())
                .commit();

        if (null == user) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
                onSetupNavigationDrawer();
            });
        } else {
            onSetupNavigationDrawer();
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
            mNavigationView.getMenu().findItem(nav_profile).setChecked(true);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        ProgramsFragment.isShowing = false;

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

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
                .addToBackStack(null)
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
                mNavigationView.getMenu().findItem(nav_profile).setVisible(false);
                mNavigationView.getMenu().findItem(R.id.nav_logout).setTitle("Back to log in");
            } else {
                Log.i("FirebaseAuth", "Regular user");
                final TextView navName =
                        (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
                final TextView navEmail =
                        (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_info);
                navName.setText(user.getDisplayName());
                navEmail.setText(user.getEmail());
            }
        }
    }

    private void logoutUser() {
        // Sign out from account manager
        FirebaseAuth.getInstance().signOut();

        // Launching the login activity
        final Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}