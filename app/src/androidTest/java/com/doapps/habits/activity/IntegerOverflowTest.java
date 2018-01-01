package com.doapps.habits.activity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.doapps.habits.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IntegerOverflowTest {

  @Rule
  public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void createAndDeleteHabit() {
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.edit_title)).perform(typeText("Running"));
    onView(withId(R.id.edit_time)).perform(typeText("50234902384329048324932423"));
    onView(withId(R.id.edit_time)).perform(pressImeActionButton());
    closeSoftKeyboard();
    onView(withId(R.id.btnCreate)).perform(click());
    onView(withId(R.id.habits_list))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
  }
}
