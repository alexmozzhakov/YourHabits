package com.doapps.habits.activity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.doapps.habits.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FacebookLoginTest {

  @Rule
  public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
      MainActivity.class);

  @Test
  public void facebookLoginTest() {
    //Login
    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withText("Login")).perform(click());
    onView(withText("Login using Facebook")).perform(click());

    try {
      Thread.sleep(6000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withText("Profile")).perform(click());
    onView(withId(R.id.btn_connect_facebook)).check(matches(not(isDisplayed())));
    //Logout
    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withText("Logout")).perform(click());
  }
}
