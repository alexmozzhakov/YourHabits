package com.habit_track.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
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
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.fragments.CalendarFragment;
import com.habit_track.fragments.CreateFragment;
import com.habit_track.fragments.HomeFragment;
import com.habit_track.fragments.ProfileFragment;
import com.habit_track.fragments.ProgramsFragment;
import com.habit_track.fragments.SettingsFragment;
import com.habit_track.fragments.StatisticsFragment;
import com.habit_track.helper.SQLiteHandler;
import com.habit_track.helper.SessionManager;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment lastFragment;
    private FragmentTransaction transaction;
    private SQLiteHandler db;
    private SessionManager session;
    private Toolbar toolbar;

    @SuppressWarnings("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        lastFragment = new HomeFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, lastFragment).commit();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView nav_name = (TextView) navigationView.findViewById(R.id.name_info);
        TextView nav_email = (TextView) navigationView.findViewById(R.id.email_info);
        nav_name.setText(user.get("name"));
        nav_email.setText(user.get("email"));
    }

    @SuppressWarnings({"StatementWithEmptyBody", "CommitTransaction"})
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        transaction = getFragmentManager().beginTransaction();
        if (id == R.id.nav_home) {
            if (!(lastFragment instanceof HomeFragment)) {
                lastFragment = new HomeFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Home");
            }
        } else if (id == R.id.nav_calendar) {
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
        } else if (id == R.id.nav_programs) {
            if (!(lastFragment instanceof ProgramsFragment)) {
                lastFragment = new ProgramsFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Programs");
            }
        } else if (id == R.id.nav_lists) {
            if (!(lastFragment instanceof ListFragment)) {
                lastFragment = new ListFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("List");
            }
        } else if (id == R.id.nav_profile) {
            if (!(lastFragment instanceof ProfileFragment)) {
                lastFragment = new ProfileFragment();
                transaction.replace(R.id.content_frame, lastFragment);
                toolbar.setTitle("Profile");
            }
        } else if (id == R.id.nav_settings) {
            toolbar.setTitle("Settings");
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void toCreateFragment(View view) {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new CreateFragment()).commit();
    }

    public void settings(MenuItem item) {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }
}
