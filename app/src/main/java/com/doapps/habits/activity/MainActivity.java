package com.doapps.habits.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.fragments.CreateFragment;
import com.doapps.habits.fragments.HomeFragment;
import com.doapps.habits.fragments.ListFragment;
import com.doapps.habits.fragments.ProfileFragment;
import com.doapps.habits.fragments.ProgramsFragment;
import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.NameChangeListener;
import com.doapps.habits.helper.PicassoRoundedTransformation;
import com.doapps.habits.listeners.MenuAvatarListener;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IMenuAvatarUpdater;
import com.doapps.habits.slider.swipeselector.PixelUtils;
import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,
        IMenuAvatarUpdater {
    private static final long INFLATE_DELAY = 200L;
    private int mLastFragment = -1;
    private NavigationView mNavigationView;
    private FirebaseUser user;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (getIntent().getAction() != null) {
            long id = getIntent().getLongExtra("id", 0);
            if (getIntent().getAction().equals("no")) {
                Log.i("Action", "No");
                Habit habit = HabitListManager.getInstance(this).getDatabase().getHabit(id);
                HabitListManager.getInstance(this).getDatabase().updateHabit(habit, 0);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_frame, new ListFragment())
                        .commit();
                mLastFragment = R.id.nav_lists;
                mToolbar.setTitle("List");
            } else if (getIntent().getAction().equals("yes")) {
                Log.i("Action", "Yes");
                Habit habit = HabitListManager.getInstance(this).getDatabase().getHabit(id);
                HabitListManager.getInstance(this).getDatabase().updateHabit(habit, 1);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_frame, new ListFragment())
                        .commit();
                mLastFragment = R.id.nav_lists;
                mToolbar.setTitle("List");
            }
        }

        if (savedInstanceState == null && mLastFragment != R.id.nav_lists) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, new HomeFragment())
                    .commit();
            mLastFragment = R.id.nav_home;
            mToolbar.setTitle("Home");
        }

        mNavigationView = (NavigationView) findViewById(R.id.navigationView);

        if (user == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
                user = FirebaseAuth.getInstance().getCurrentUser();
                onSetupNavigationDrawer(user);
            });
        } else {
            onSetupNavigationDrawer(user);
            NameChangeListener.listener.
                    addObserver((observable, o) -> {
                        TextView navName = (TextView)
                                mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
                        navName.setText(user.getDisplayName());
                    });
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                if (mLastFragment == R.id.nav_profile) {
                    closeImm();
                }
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logoutUser();
        } else if (mLastFragment != id) {
            if (mLastFragment == R.id.nav_profile) {
                findViewById(R.id.toolbar_shadow).setVisibility(View.VISIBLE);
            } else if (mLastFragment == R.id.nav_programs) {
                ProgramsFragment.isShowing = false;
            }

            new Handler().postDelayed(() -> {
                FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                if (id == R.id.nav_home) {
                    transaction.replace(R.id.content_frame, new HomeFragment()).commit();
                    mToolbar.setTitle("Home");
                } else if (id == R.id.nav_programs) {
                    transaction.replace(R.id.content_frame, new ProgramsFragment()).commit();
                    mToolbar.setTitle("Programs");

                } else if (id == R.id.nav_lists) {
                    transaction.replace(R.id.content_frame, new ListFragment()).commit();
                    mToolbar.setTitle("List");
                } else if (id == R.id.nav_profile) {
                    transaction.replace(R.id.content_frame, new ProfileFragment()).commit();
                    mToolbar.setTitle("Profile");
                }
                mLastFragment = id;
            }, INFLATE_DELAY);
        }

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void toCreateFragment(@SuppressWarnings("UnusedParameters") View view) {
        mNavigationView.getMenu().getItem(0).setChecked(false);
        mLastFragment = 5;
        mToolbar.setTitle("Create Habit");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new CreateFragment())
                .commit();

    }

    private void toProfile(@SuppressWarnings("UnusedParameters") View view) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if (mLastFragment == R.id.nav_profile) {
            return;
        }
        new Handler().postDelayed(() -> {
            mToolbar.setTitle("Profile");
            mNavigationView.getMenu().getItem(3).setChecked(true);

            findViewById(R.id.toolbar_shadow).setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new ProfileFragment())
                    .commit();
            mLastFragment = R.id.nav_profile;
        }, INFLATE_DELAY);
    }

    @Override
    public void updateMenuAvatar(Uri uri) {
        ImageView avatar =
                (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_photo);
        if (BuildConfig.DEBUG) {
            Log.i("updateMenuAvatar", String.valueOf(uri));
        }
        Picasso.with(getApplicationContext())
                .load(uri)
                .transform(new PicassoRoundedTransformation())
                .into(avatar);
        avatar.invalidate();
    }

    public void onSetupNavigationDrawer(FirebaseUser user) {

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        if (user != null && user.isAnonymous()) {
            Log.i("FirebaseAuth", "User is anonymous");
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_logout).setTitle("Login");
            mNavigationView.getHeaderView(0).setVisibility(View.GONE);
        } else if (user != null) {
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
            Log.i("FirebaseAuth", "Regular user");
            TextView navName =
                    (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_info);
            TextView navEmail =
                    (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_info);
            navName.setText(user.getDisplayName());
            navEmail.setText(user.getEmail());
            mNavigationView.getHeaderView(0).setOnClickListener(this::toProfile);
            if (user.getPhotoUrl() != null) {
                mNavigationView.getHeaderView(0)
                        .findViewById(R.id.fields_info).setPadding(
                        (int) PixelUtils.dpToPixel(this, 72), 0, 0, 0);
                ImageView avatar =
                        (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_photo);

                AvatarManager.listener.invalidateUrl();
                Log.i("updateMenuAvatar", String.valueOf(AvatarManager.listener.getMediumUri()));
                Picasso.with(getApplicationContext())
                        .load(AvatarManager.listener.getMediumUri())
                        .transform(new PicassoRoundedTransformation())
                        .into(avatar);
                avatar.setVisibility(View.VISIBLE);
            }
        } else {
            Log.w("FirebaseAuth", "User is null");
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            mNavigationView.getHeaderView(0).setVisibility(View.GONE);
        }
        if (AvatarManager.listener.countObservers() == 0) {
            AvatarManager.listener.addObserver(new MenuAvatarListener(this));
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
            FirebaseAuth.getInstance().signOut();
            Log.i("FA", "user was signed out");
        }
        // Launching the login activity
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    public void closeImm() {
        InputMethodManager imm = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        Log.i("IMM", "Closed imm");
    }

    public static boolean isFacebook(FirebaseUser user) {
        if (user.getProviders() != null) {
            for (String provider : user.getProviders()) {
                if (provider.contains("facebook")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        AvatarManager.listener.deleteObservers();
        super.onDestroy();
    }
}
