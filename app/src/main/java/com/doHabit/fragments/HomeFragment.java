package com.dohabit.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.dohabit.R;
import com.dohabit.adapter.TimeLineAdapter;
import com.dohabit.helper.ConnectionManager;
import com.dohabit.helper.HabitListManager;
import com.dohabit.helper.HomeDayManager;
import com.dohabit.models.Habit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private TextView weather;
    private TextView weatherBot;

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String URL_WEATHER_API = "http://habbitsapp.esy.es/weather_api.php";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        weather = (TextView) view.findViewById(R.id.weather);
        weatherBot = (TextView) view.findViewById(R.id.weatherBot);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        sharedPreferences = getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            weather.setText(sharedPreferences.getString("celsius", "18"));
            weatherBot.setText(sharedPreferences.getString("location", "Error getting\n location"));
        }

        if (ConnectionManager.isConnected(getContext())) {
            getWeather();
        } else {
            new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    final ConnectivityManager conMan = (ConnectivityManager)
                            context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    final NetworkInfo netInfo = conMan.getActiveNetworkInfo();

                    if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        getWeather();
                    }
                }
            }.goAsync();
        }


        final List<Habit> habitList = HabitListManager.getInstance(getContext());
        final TextView tasksDue = (TextView) view.findViewById(R.id.tasks_due);
        tasksDue.setText(getDueCount(habitList));

        // Inflate the layout for this fragment
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final TimeLineAdapter timeLineAdapter = new TimeLineAdapter(habitList);
        timeLineAdapter.setHasStableIds(true);
        recyclerView.setAdapter(timeLineAdapter);
        initDaysTabs(tabLayout, timeLineAdapter);
        return view;

    }

    private static void initDaysTabs(final TabLayout tabLayout, final TimeLineAdapter timeLineAdapter) {
        if (tabLayout != null) {
            final String[] daysOfWeek =
                    {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            Log.i("HomeFragment", String.format("dayOfWeek = %s", daysOfWeek[dayOfWeek]));
            for (int i = 0; i < 7; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(daysOfWeek[(dayOfWeek + i) % 7]));
            }

            tabLayout.setSmoothScrollingEnabled(true);
            tabLayout.setScrollPosition(0, 0.0f, true);
            final TabLayout.Tab tab = tabLayout.getTabAt(0);

            if (tab != null) {
                tab.select();
            }

            final HomeDayManager homeDayManager =
                    new HomeDayManager(timeLineAdapter);

            tabLayout.addOnTabSelectedListener(
                    new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(final TabLayout.Tab tab) {
                            if (tab.getPosition() != 0) {
                                final int day = (dayOfWeek + tab.getPosition()) % 7;
                                Log.i("HomeFragment", "Day = " + daysOfWeek[day]);

                                homeDayManager.updateListByDay(HabitListManager.getHabitsList(), day);

                            }

                        }

                        @Override
                        public void onTabUnselected(final TabLayout.Tab tab) {
                            // ignored
                        }

                        @Override
                        public void onTabReselected(final TabLayout.Tab tab) {
                            // ignored
                        }
                    }

            );
        }
    }

    private static CharSequence getDueCount(final Iterable<Habit> habitsList) {
        final Calendar calendar = Calendar.getInstance();
        final int date = calendar.get(Calendar.DATE);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (final Habit habit : habitsList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }
        return String.valueOf(counter);
    }

    private void getWeather() {
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(
                new StringRequest(Request.Method.GET, URL_WEATHER_API,
                        response -> {
                            try {
                                final JSONObject o = new JSONObject(response);

                                if (o.has("error")) {
                                    Log.e("JSONException", "Response got: " + o.getString("error"));
                                } else {
                                    sharedPreferences.edit().putString("celsius",
                                            String.format("%s˚C", o.getString("celsius"))).apply();

                                    sharedPreferences.edit().putString("location",
                                            o.getString("location")).apply();

                                    weather.setText(o.getString("celsius") + "˚C");
                                    weatherBot.setText(o.getString("location"));
                                }

                            } catch (final JSONException e) {
                                Log.e("JSONException", "Response got: " + response, e);
                            }

                        },
                        error -> Log.e("StringRequest error", error.toString()))
        );
    }
}