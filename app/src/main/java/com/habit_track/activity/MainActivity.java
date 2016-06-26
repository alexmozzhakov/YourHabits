package com.habit_track.activity;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.habit_track.R;
import com.habit_track.fragments.CreateFragment;
import com.habit_track.fragments.HomeFragment;
import com.habit_track.fragments.ListFragment;
import com.habit_track.fragments.ProfileFragment;
import com.habit_track.fragments.ProgramsFragment;
import com.habit_track.helper.AccountManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // TODO: 16/06/2016 move navigation logic to another class
    private int lastFragment = -1;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @SuppressWarnings("CommitTransaction")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("Home");
        }

        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (mDrawerLayout != null) {
            mDrawerLayout.addDrawerListener(toggle);
        }
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            mNavigationView.getMenu().getItem(0).setChecked(true);
        }


        Fragment fragment = getSupportFragmentManager().
                findFragmentById(R.id.content_frame);
        if (fragment == null) {
            fragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }

//        mTransaction = getSupportFragmentManager().beginTransaction();
//        mTransaction.replace(R.id.content_frame, new HomeFragment()).commit();

//        final TextView navName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
//        final TextView navEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_info);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null && navName != null && navEmail != null) {
//            navName.setText(user.getDisplayName());
//            navEmail.setText(user.getEmail());
//        }

    }

    @SuppressWarnings("CommitTransaction")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.

        final int id = item.getItemId();

        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_home) {
            mTransaction.replace(R.id.content_frame, new HomeFragment());
            mToolbar.setTitle("Home");
            lastFragment = 0;
            mNavigationView.getMenu().findItem(id).setChecked(true);

        } else if (id == R.id.nav_programs) {
            mTransaction.replace(R.id.content_frame, new ProgramsFragment());
            mToolbar.setTitle("Programs");
            lastFragment = 1;
            mNavigationView.getMenu().findItem(id).setChecked(true);

        } else if (id == R.id.nav_lists) {
            mTransaction.replace(R.id.content_frame, new ListFragment());
            mToolbar.setTitle("List");
            lastFragment = 2;
            mNavigationView.getMenu().findItem(id).setChecked(true);
        } else if (id == R.id.nav_profile) {
            mTransaction.replace(R.id.content_frame, new ProfileFragment());
            mToolbar.setTitle("Profile");
            lastFragment = 3;
            mNavigationView.getMenu().findItem(id).setChecked(true);
        } else if (id == R.id.nav_logout) {
            AccountManager.logoutUser(this);
        }

        ProgramsFragment.isShowing = false;

        mTransaction.commit();

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new CreateFragment())
                .commit();

    }

    public void toProfile(final View view) {
        mDrawerLayout.closeDrawers();
        mToolbar.setTitle("Profile");

        if (lastFragment != -1) {
            mNavigationView.getMenu().getItem(lastFragment).setChecked(false);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new ProfileFragment())
                .commit();
    }

}