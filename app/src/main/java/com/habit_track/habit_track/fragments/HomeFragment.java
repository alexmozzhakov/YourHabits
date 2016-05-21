package com.habit_track.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.habit_track.R;
import com.habit_track.habit_track.app.AppController;
import com.habit_track.habit_track.app.Habit;
import com.habit_track.habit_track.helper.HabitDBHandler;
import com.habit_track.habit_track.helper.TimeLineAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView weather = (TextView) view.findViewById(R.id.weather);
        final TextView weatherBot = (TextView) view.findViewById(R.id.weatherBot);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        if (tabLayout != null) {
            final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < 7; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(daysOfWeek[(dayOfWeek + i) % 7]));
            }

            tabLayout.setSmoothScrollingEnabled(true);
            tabLayout.setScrollPosition(0, 0f, true);
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            weather.setText(sharedPreferences.getString("celsius", "18"));
            weatherBot.setText(sharedPreferences.getString("location", "Error getting\n location"));
        }

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(
                new StringRequest(Request.Method.GET, AppController.URL_WEATHER_API,
                        response -> {
                            try {
                                final JSONObject o = new JSONObject(response);
                                final SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("celsius", o.getString("celsius").concat("˚C")).apply();
                                editor.putString("location", o.getString("location")).apply();

                                weather.setText(o.getString("celsius") + "˚C");
                                weatherBot.setText(o.getString("location"));

                            } catch (JSONException e) {
                                Log.e("JSONException", "response error", e);
                            }

                        },
                        error -> Log.e("StringRequest error", error.toString()))
        );


        if (ListFragment.mHabitsDatabase == null) {
            ListFragment.mHabitsDatabase = new HabitDBHandler(this.getActivity());
        }

        Log.i("some", "onCreateView");
        if (ListFragment.mHabitsDatabase.notSame() || ListFragment.mHabitsList == null) {
            ListFragment.mHabitsList = ListFragment.mHabitsDatabase.getHabitDetailsAsArrayList();
        }

        final Calendar calendar = Calendar.getInstance();
        final int date = calendar.get(Calendar.DATE);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (final Habit habit : ListFragment.mHabitsList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }

        final TextView tasksDue = (TextView) view.findViewById(R.id.tasks_due);
        tasksDue.setText(String.valueOf(counter));


        // Inflate the layout for this fragment
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final TimeLineAdapter timeLineAdapter = new TimeLineAdapter(ListFragment.mHabitsList);
        recyclerView.setAdapter(timeLineAdapter);
        return view;

    }

}