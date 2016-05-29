package com.habit_track.activity;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.habit_track.R;
import com.habit_track.database.HabitDBHandler;
import com.habit_track.fragments.CreateFragment;
import com.habit_track.fragments.HomeFragment;
import com.habit_track.fragments.ListFragment;
import com.habit_track.fragments.ProfileFragment;
import com.habit_track.fragments.ProgramsFragment;

import java.util.Calendar;

import static com.habit_track.helper.AppManager.mHabitsDatabase;
import static com.habit_track.helper.AppManager.mLastFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean immOpened;
    private NavigationView mNavigationView;
    private FragmentTransaction mTransaction;
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
        mLastFragment = new HomeFragment();

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

        mTransaction = getFragmentManager().beginTransaction();
        mTransaction.replace(R.id.content_frame, mLastFragment).commit();

        final TextView navName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
        final TextView navEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_info);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            navEmail.setText(user.getEmail());
            UserProfileChangeRequest.Builder changeRequest = new UserProfileChangeRequest.Builder();
            changeRequest.setDisplayName("some");
            user.updateProfile(changeRequest.build());
            navName.setText(user.getDisplayName());
        }

    }

    @SuppressWarnings("CommitTransaction")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.

        final int id = item.getItemId();

        mTransaction = getFragmentManager().beginTransaction();
        if (id == R.id.nav_home && !(mLastFragment instanceof HomeFragment)) {
            mTransaction.remove(mLastFragment);
            mLastFragment = new HomeFragment();
            mTransaction.replace(R.id.content_frame, mLastFragment);
            mToolbar.setTitle("Home");
            mNavigationView.getMenu().findItem(id).setChecked(true);

        } else if (id == R.id.nav_programs && !(mLastFragment instanceof ProgramsFragment)) {
            mTransaction.remove(mLastFragment);
            mLastFragment = new ProgramsFragment();
            mTransaction.replace(R.id.content_frame, mLastFragment);
            mToolbar.setTitle("Programs");
            mNavigationView.getMenu().findItem(id).setChecked(true);

        } else if (id == R.id.nav_lists && !(mLastFragment instanceof ListFragment)) {
            mTransaction.remove(mLastFragment);
            mLastFragment = new ListFragment();
            mTransaction.replace(R.id.content_frame, mLastFragment);
            mToolbar.setTitle("List");
            mNavigationView.getMenu().findItem(id).setChecked(true);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        final View view = getCurrentFocus();

        if (view != null && immOpened) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Log.i("IMM", "Closed imm");
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

    private void logoutUser() {
        // Sign out from account manager
        FirebaseAuth.getInstance().signOut();

        // Launching the login activity
        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void toCreateFragment(final View view) {
        mNavigationView.getMenu().getItem(0).setChecked(false);
        mLastFragment = new CreateFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, mLastFragment).commit();
    }


    public void onCreate(final View view) {

        final EditText editTitle = (EditText) findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        final EditText editTime = (EditText) findViewById(R.id.editTime);

        // Checks what data was entered and adds habit to mUserDatabase
        if (editDescription != null && editTitle != null && editTime != null) {
            if (mHabitsDatabase == null) {
                mHabitsDatabase = new HabitDBHandler(this);
            }

            int time;
            if (!editTime.getText().toString().equals("")) {
                time = Integer.valueOf(editTime.getText().toString());
            } else {
                time = 60;
            }

            mHabitsDatabase.addHabit(
                    editTitle.getText().toString(),
                    editDescription.getText().toString(),
                    time,
                    false,
                    Calendar.getInstance(),
                    0, 0, 0
            );
        }

        // Closes keyboard if created new habit
        if (immOpened) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Log.i("IMM", "Closed imm");
        }

        HabitDBHandler.isChecked = false;
        // Creates new ListFragment and replaces CreateFragment
        mLastFragment = new ListFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, mLastFragment).commit();
    }

    public void toProfile(View view) {
        mDrawerLayout.closeDrawers();
        mToolbar.setTitle("Profile");

        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            if (mNavigationView.getMenu().getItem(i).isChecked()) {
                mNavigationView.getMenu().getItem(i).setChecked(false);
                break;
            }
        }

        mLastFragment = new ProfileFragment();

        getFragmentManager().beginTransaction().replace(R.id.content_frame, mLastFragment).commit();
    }

}