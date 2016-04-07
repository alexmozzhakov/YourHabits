package com.habit_track.activity;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.fragments.CreateFragment;
import com.habit_track.fragments.HomeFragment;
import com.habit_track.fragments.ListFragment;
import com.habit_track.fragments.ProgramsFragment;
import com.habit_track.helper.SQLiteHandler;
import com.habit_track.helper.SessionManager;

import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static android.app.Fragment lastFragment;
    private FragmentTransaction transaction;
    private SQLiteHandler database;
    private SessionManager session;
    private Toolbar toolbar;
    public static NavigationView navigationView;

    @SuppressWarnings("CommitTransaction")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Home");
        }


        setSupportActionBar(toolbar);
        lastFragment = new HomeFragment();


        // SqLite database handler
        database = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, lastFragment).commit();

        // Fetching user details from SQLite
        final Map<String, String> user = database.getUserDetails();

        final TextView nav_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name_info);
        final TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_info);
        nav_name.setText(user.get("name"));
        nav_email.setText(user.get("email"));


    }

    @SuppressWarnings({"StatementWithEmptyBody", "CommitTransaction"})
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        transaction = getFragmentManager().beginTransaction();
        if (id == R.id.nav_home) {
            if (!(lastFragment instanceof HomeFragment)) {
                lastFragment = new HomeFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Home");
                navigationView.getMenu().findItem(id).setChecked(true);
            }
        } /*else if (id == R.id.nav_calendar) {
            if (!(lastFragment instanceof CalendarFragment)) {
                lastFragment = new CalendarFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Calendar");
            }
        } else if (id == R.id.nav_stats) {
            if (!(lastFragment instanceof StatisticsFragment)) {
                lastFragment = new StatisticsFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Statistics");
            }
        } */ else if (id == R.id.nav_programs) {
            if (!(lastFragment instanceof ProgramsFragment)) {
                lastFragment = new ProgramsFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Programs");
                navigationView.getMenu().findItem(id).setChecked(true);
            }
        } else if (id == R.id.nav_lists) {
            if (!(lastFragment instanceof ListFragment)) {
                lastFragment = new ListFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("List");
                navigationView.getMenu().findItem(id).setChecked(true);
            }
        } /*else if (id == R.id.nav_profile) {
            if (!(lastFragment instanceof ProfileFragment)) {
                lastFragment = new ProfileFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Profile");
            }
        } else if (id == R.id.nav_settings) {
            if (!(lastFragment instanceof SettingsFragment)) {
                lastFragment = new SettingsFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Settings");
            }
        }*/ else if (id == R.id.nav_logout) {
            logoutUser();
        }
        transaction.commit();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logoutUser() {
        session.setLogin(false);

        database.deleteUsers();

        // Launching the login activity
        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void toCreateFragment(final View view) {
        navigationView.getMenu().getItem(0).setChecked(false);
        lastFragment = new CreateFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, lastFragment).commit();
    }


    public void onCreate(final View view) {
        final EditText editTitle = (EditText) findViewById(R.id.editTitle);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);

        if (editDescription != null && editTitle != null) {
            ListFragment.habitsDatabase.addHabit(editTitle.getText().toString(), editDescription.getText().toString(), 60, false, Calendar.getInstance());
        }


        final InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        lastFragment = new ListFragment();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, lastFragment).commit();
    }
}
