package com.doapps.habits.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
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
import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.adapter.TimeLineAdapter;
import com.doapps.habits.helper.ConnectionManager;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.HomeDayManager;
import com.doapps.habits.models.DayManager;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.HabitListProvider;
import com.doapps.habits.models.UpdatableList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView weather;
    private TextView weatherBot;

    @SuppressWarnings("HardCodedStringLiteral")
    private static final String URL_WEATHER_API = "http://habbitsapp.esy.es/weather_api.php";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        weather = (TextView) view.findViewById(R.id.weather);
        weatherBot = (TextView) view.findViewById(R.id.weatherBot);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        final HabitListProvider habitListManager =
                new HabitListManager(getContext().getApplicationContext());

        final List<Habit> habitList = new ArrayList<>(habitListManager.getList());
        final TextView tasksDue = (TextView) view.findViewById(R.id.tasks_due);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        // get day of week
        final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        // filter list for today
        HomeDayManager.filterListForToday(habitList, String.valueOf(dayOfWeek));
        // set due count of filtered list
        tasksDue.setText(getDueCount(habitList));

        // TODO: 06/07/2016 refactor this part
        if (ConnectionManager.isConnected(getContext())) {
            getWeather();
        } else {
            weather.setText(String.valueOf(habitList.size()));
            weatherBot.setText("All tasks");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final ConnectivityManager conMan = (ConnectivityManager)
                        getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                Log.i("Lol", "addDefaultNetworkActiveListener");
                conMan.addDefaultNetworkActiveListener(this::getWeather);
            }
        }

        // set filtered list to adapter
        final TimeLineAdapter timeLineAdapter = new TimeLineAdapter(habitList);
        timeLineAdapter.setHasStableIds(true);
        recyclerView.setAdapter(timeLineAdapter);

        final String[] daysOfWeek =
                getActivity().getResources().getStringArray(R.array.days_of_week_array);
        initDaysTabs(daysOfWeek, dayOfWeek, tabLayout, timeLineAdapter, new HabitListManager(getContext()));
        return view;

    }

    private static void initDaysTabs(final String[] daysOfWeek,
                                     final int dayOfWeek,
                                     final TabLayout tabLayout,
                                     final UpdatableList<Habit> timeLineAdapter,
                                     final HabitListProvider habitListProvider) {
        if (tabLayout != null) {
            Log.i("HomeFragment", String.format("dayOfWeek = %s", daysOfWeek[dayOfWeek]));
            for (int i = 0; i < 7; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(daysOfWeek[(dayOfWeek + i) % 7]));
            }

            final DayManager<Habit> homeDayManager = new HomeDayManager(timeLineAdapter);

            tabLayout.setSmoothScrollingEnabled(true);
            tabLayout.setScrollPosition(0, 0.0f, true);

            tabLayout.addOnTabSelectedListener(
                    new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(final TabLayout.Tab tab) {
                            if (tab.getPosition() == 0) {
                                homeDayManager.updateForToday(habitListProvider.getList(), dayOfWeek);
                            } else {
                                final int day = (dayOfWeek + tab.getPosition()) % 7;

                                if (BuildConfig.DEBUG) {
                                    Log.i("HomeFragment", "Day = " + daysOfWeek[day] + ", num = " + day);
                                }

                                homeDayManager.updateListByDay(habitListProvider.getList(), day);
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