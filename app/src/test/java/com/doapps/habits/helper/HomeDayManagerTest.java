package com.doapps.habits.helper;

import com.doapps.habits.models.Habit;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("HardCodedStringLiteral")
public class HomeDayManagerTest {
    private static List<Habit> generateListOfHabits() {
        final List<Habit> list = new ArrayList<>(15);
        //once types
        list.add(new Habit(1, "once", "", false, 12, 12, 12, 60, 30, 30, new int[]{1, 0}));
        list.add(new Habit(1, "once a week", "", false, 12, 12, 12, 60, 30, 30, new int[]{1, 7}));
        list.add(new Habit(1, "once a year", "", false, 12, 12, 12, 60, 30, 30, new int[]{1, 365}));
        //every ... types
        list.add(new Habit(1, "every sunday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every monday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every tuesday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every wednesday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every thursday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every friday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every saturday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every day", "", false, 12, 12, 12, 60, 30, 30, new int[]{1, 1}));
        //every ... and ... types
        list.add(new Habit(1, "every sunday and saturday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every monday and friday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every tuesday and thursday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        list.add(new Habit(1, "every wednesday and thursday", "", false, 12, 12, 12, 60, 30, 30, new int[]{1,8,1}));
        return list;
    }

    @Test
    public void filterForToday() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListForToday(list);
        System.out.println("today : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("sunday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 6, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForSun() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 1);
        System.out.println("sun : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title), habit.title.contains("sunday")
                    || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForMon() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 2);
        System.out.println("mon : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("monday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForTue() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 3);
        System.out.println("tue : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("tuesday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForWed() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 4);
        System.out.println("wed : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("wednesday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForThu() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 5);
        System.out.println("thu : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("thursday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 4, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForFri() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 6);
        System.out.println("fri : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("friday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }

    @Test
    public void filterListForSat() {
        final List<Habit> list = generateListOfHabits();
        HomeDayManager.filterListByDay(list, 7);
        System.out.println("sat : {");
        for (final Habit habit : list) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("saturday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        Assert.assertThat(String.format(Locale.ENGLISH, "List size = %d, here are them: %s", list.size(),
                list),
                list.size() == 3, CoreMatchers.is(true));
        System.out.println("}");
    }
}