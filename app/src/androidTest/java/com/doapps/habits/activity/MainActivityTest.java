package com.doapps.habits.activity;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.doapps.habits.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createAndDeleteHabit() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.edit_title)).perform(typeText("Running"));
        onView(withId(R.id.edit_time)).perform(typeText("50")).perform(pressImeActionButton());
        closeSoftKeyboard();
        onView(withId(R.id.btnCreate)).perform(click());
        onView(withId(R.id.habits_list)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout)).perform(swipeRight());
        openDrawer(R.id.drawer_layout);
        onView(withText("Home")).perform(click());
        onView(withText("Running")).check(matches(isDisplayed()));
        openDrawer(R.id.drawer_layout);
        onView(withText("Lists")).perform(click());
        onView(withId(R.id.habits_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
        openDrawer(R.id.drawer_layout);
        onView(withText("Home")).perform(click());
        openDrawer(R.id.drawer_layout);
        onView(withText("Lists")).perform(click());
        onView(withId(R.id.empty_view)).check(matches(isDisplayed()));
    }
}