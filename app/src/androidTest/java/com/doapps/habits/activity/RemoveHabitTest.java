package com.doapps.habits.activity;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.doapps.habits.R;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RemoveHabitTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void removeHabitTest() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText("Lists")).perform(click());
        onView(withId(R.id.habits_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
    }

    @Test
    public void removeAllHabitsTest() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText("Lists")).perform(click());
        removeAll();
        onView(withText(R.string.no_habits_available)).check(matches(isDisplayed()));
    }

    private static void removeAll() {
        try {
            onView(withText(R.string.no_habits_available)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            onView(withId(R.id.habits_list))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
            removeAll();
        }
    }
}