package com.doapps.habits.activity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.doapps.habits.activity.TestUtilsKt.removeFirstHabit;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.doapps.habits.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FilterTest {

  @Rule
  public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void createHabitAndChangeDay() {
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.edit_title)).perform(typeText("Running"));
    onView(withId(R.id.edit_time)).perform(typeText("20"));
    onView(withId(R.id.edit_time)).perform(pressImeActionButton());
    closeSoftKeyboard();
    onView(withId(R.id.sFrequency)).perform(click());
    onView(withText("Every week")).perform(click());
    onView(withId(R.id.btnCreate)).perform(click());
    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withText("Home")).perform(click());
    onView(withText("Running")).check(matches(isDisplayed()));
    onView(withId(R.id.swipe_selector_layout_end_button)).perform(click());
    onView(withText("Running")).check(doesNotExist());
    removeFirstHabit();
  }
}
