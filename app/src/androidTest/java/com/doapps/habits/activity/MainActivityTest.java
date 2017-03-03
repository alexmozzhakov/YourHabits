package com.doapps.habits.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import com.doapps.habits.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String TAG = MainActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createAndDeleteHabit() {
        takeScreenshot("start", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.fab)).perform(click());
        takeScreenshot("creation", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.edit_title)).perform(typeText("Running"));
        takeScreenshot("title_typing", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.edit_time)).perform(typeText("50")).perform(pressImeActionButton());
        takeScreenshot("time_typing", mainActivityActivityTestRule.getActivity());
        closeSoftKeyboard();
        onView(withId(R.id.btnCreate)).perform(click());
        takeScreenshot("after_created", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.habits_list)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(swipeRight());
        takeScreenshot("habit_deleted", mainActivityActivityTestRule.getActivity());
        openDrawer(R.id.drawer_layout);
        takeScreenshot("menu_open", mainActivityActivityTestRule.getActivity());
        onView(withText("Home")).perform(click());
        takeScreenshot("to_home", mainActivityActivityTestRule.getActivity());
        onView(withText("Running")).check(matches(isDisplayed()));
        doRestart(mainActivityActivityTestRule.getActivity());
        openDrawer(R.id.drawer_layout);
        takeScreenshot("second_menu_open", mainActivityActivityTestRule.getActivity());
        onView(withText("Lists")).perform(click());
        takeScreenshot("list_open", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.habits_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        openDrawer(R.id.drawer_layout);
        onView(withText("Home")).perform(click());
        takeScreenshot("back_home", mainActivityActivityTestRule.getActivity());
        openDrawer(R.id.drawer_layout);
        onView(withText("Lists")).perform(click());
        takeScreenshot("back_list", mainActivityActivityTestRule.getActivity());
        onView(withId(R.id.empty_view)).check(matches(isDisplayed()));
    }

    public static void takeScreenshot(String name, Activity activity) {

        // In Testdroid Cloud, taken screenshots are always stored
        // under /test-screenshots/ folder and this ensures those screenshots
        // be shown under Test Results
        String path =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/test-screenshots/" + name + ".png";

        View scrView = activity.getWindow().getDecorView().getRootView();
        scrView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(scrView.getDrawingCache());
        scrView.setDrawingCacheEnabled(false);

        OutputStream out = null;
        File imageFile = new File(path);

        try {
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // exception
        } catch (IOException e) {
            // exception
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
            }

        }
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }
    }
}