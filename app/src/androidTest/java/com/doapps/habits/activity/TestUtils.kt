package com.doapps.habits.activity

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeRight
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.v7.widget.RecyclerView
import com.doapps.habits.R

fun removeFirstHabit() {
  onView(withId(R.id.drawer_layout)).perform(open())
  onView(withText("List")).perform(click())
  onView(withId(R.id.habits_list))
      .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))
}