package com.doapps.habits.activity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.doapps.habits.R;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import tools.fastlane.screengrab.locale.LocaleTestRule;

@RunWith(AndroidJUnit4.class)
public class ProgramsAddTest {

  @ClassRule
  public static final LocaleTestRule localeTestRule = new LocaleTestRule();

  @Rule
  public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void DoubleAddProgram() {
    onView(withId(R.id.drawer_layout)).perform(open());
    onView(withText("Programs")).perform(click());
    onView(withText("Running")).perform(click());
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.fab)).perform(click());
  }

}