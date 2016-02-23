package com.habitart.yourhabits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.habitart.yourhabits.R;
import com.habitart.yourhabits.helper.SQLiteHandler;
import com.habitart.yourhabits.helper.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteHandler db;
    private SessionManager session;
    public String name;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtName = (TextView) findViewById(R.id.name);
        TextView txtEmail = (TextView) findViewById(R.id.email);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        email = user.get("email");
        LoginActivity.lastLoginEmail = email;
        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.vf);

        if (id == R.id.nav_home) {
            vf.setDisplayedChild(0);
        } else if (id == R.id.nav_calendar) {
            vf.setDisplayedChild(1);
        } else if (id == R.id.nav_stats) {
            vf.setDisplayedChild(2);
        } else if (id == R.id.nav_programs) {
            vf.setDisplayedChild(3);
        } else if (id == R.id.nav_lists) {
            vf.setDisplayedChild(4);
        } else if (id == R.id.nav_profile) {
            vf.setDisplayedChild(5);
        } else if (id == R.id.nav_settings) {
            vf.setDisplayedChild(6);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}