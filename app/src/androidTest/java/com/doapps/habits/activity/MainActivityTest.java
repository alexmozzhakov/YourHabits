package com.doapps.habits.activity;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.doapps.habits.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     * TAG is defined for logging errors and debugging information
     */
    private static final String TAG = MainActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createAndDeleteHabit() {

        onView(withId(R.id.fab)).perform(click());
        takeScreenshot();
        onView(withId(R.id.edit_title)).perform(typeText("Running"));
        takeScreenshot();
        onView(withId(R.id.edit_time)).perform(typeText("50")).perform(pressImeActionButton());
        takeScreenshot();
        closeSoftKeyboard();
        onView(withId(R.id.btnCreate)).perform(click());
        takeScreenshot();
        onView(withId(R.id.habits_list)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(swipeRight());
        takeScreenshot();
        onView(withId(R.id.drawer_layout)).perform(open());
        takeScreenshot();
        onView(withText("Home")).perform(click());
        takeScreenshot();
        onView(withText("Running")).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(open());
        takeScreenshot();
        onView(withText("Lists")).perform(click());
        takeScreenshot();
        onView(withId(R.id.habits_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText("Home")).perform(click());
        takeScreenshot();
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText("Lists")).perform(click());
        takeScreenshot();
        onView(withId(R.id.empty_view)).check(matches(isDisplayed()));
    }

    private void takeScreenshot() {
        Date now = new Date();
        //TODO: assign value
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + now + ".jpg";
            Log.i("Path", mPath);
            // create bitmap screen capture
            View view = mainActivityActivityTestRule.getActivity().findViewById(android.R.id.content);
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            Log.e(TAG, e.getMessage());
        }
    }
}