package com.dohabit.helper;

import com.dohabit.adapter.TimeLineAdapter;
import com.dohabit.models.Habit;
import com.dohabit.models.HabitListProvider;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"HardCodedStringLiteral", "LocalVariableOfConcreteClass", "TypeMayBeWeakened"})
public class HomeDayManagerTest {
    private static List<Habit> generateListOfHabits() {
        final List<Habit> list = new ArrayList<>(15);
        //once types
        list.add(new Habit(1, "once", "", false, 12, 12, 12, 60, 30, 30, 100));
        list.add(new Habit(1, "once a week", "", false, 12, 12, 12, 60, 30, 30, 107));
        list.add(new Habit(1, "once a year", "", false, 12, 12, 12, 60, 30, 30, 10365));
        //every ... types
        list.add(new Habit(1, "every sunday", "", false, 12, 12, 12, 60, 30, 30, 10807));
        list.add(new Habit(1, "every monday", "", false, 12, 12, 12, 60, 30, 30, 20807));
        list.add(new Habit(1, "every tuesday", "", false, 12, 12, 12, 60, 30, 30, 30807));
        list.add(new Habit(1, "every wednesday", "", false, 12, 12, 12, 60, 30, 30, 40807));
        list.add(new Habit(1, "every thursday", "", false, 12, 12, 12, 60, 30, 30, 50807));
        list.add(new Habit(1, "every friday", "", false, 12, 12, 12, 60, 30, 30, 60807));
        list.add(new Habit(1, "every saturday", "", false, 12, 12, 12, 60, 30, 30, 70807));
        list.add(new Habit(1, "every day", "", false, 12, 12, 12, 60, 30, 30, 101));
        //every ... and ... types
        list.add(new Habit(1, "every sunday and saturday", "", false, 12, 12, 12, 60, 30, 30, 1070801));
        list.add(new Habit(1, "every monday and friday", "", false, 12, 12, 12, 60, 30, 30, 2060807));
        list.add(new Habit(1, "every tuesday and thursday", "", false, 12, 12, 12, 60, 30, 30, 3050807));
        list.add(new Habit(1, "every wednesday and thursday", "", false, 12, 12, 12, 60, 30, 30, 4050807));
        return list;
    }

    @Test
    public void filterListForSun() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "1");
        System.out.println("sun : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("sunday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForMon() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "2");
        System.out.println("mon : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("monday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForTue() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "3");
        System.out.println("tue : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("tuesday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForWed() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "4");
        System.out.println("wed : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("wednesday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForThu() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "5");
        System.out.println("thu : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("thursday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForFri() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "6");
        System.out.println("fri : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("friday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }

    @Test
    public void filterListForSat() throws Exception {
        final List<Habit> list = generateListOfHabits();
        final HabitListProvider adapter = new TimeLineAdapter(list);
        HomeDayManager.filterListByDay(list, "7");
        System.out.println("sat : {");
        for (final Habit habit : adapter.getList()) {
            Assert.assertThat(String.format("  %s%n", habit.title),
                    habit.title.contains("once") || habit.title.contains("saturday")
                            || habit.title.contains("every day"), CoreMatchers.is(true));
        }
        System.out.println("}");
    }
}