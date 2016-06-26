package com.habit_track.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.crash.FirebaseCrash;
import com.habit_track.R;
import com.habit_track.adapter.TimeLineAdapter;
import com.habit_track.helper.HabitListManager;
import com.habit_track.helper.HomeDayManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private static final String URL_WEATHER_API = "http://habbitsapp.esy.es/weather_api.php";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView weather = (TextView) view.findViewById(R.id.weather);
        final TextView weatherBot = (TextView) view.findViewById(R.id.weatherBot);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        final SharedPreferences sharedPreferences = this.getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            weather.setText(sharedPreferences.getString("celsius", "18"));
            weatherBot.setText(sharedPreferences.getString("location", "Error getting\n location"));
        }

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(
                new StringRequest(Request.Method.GET, URL_WEATHER_API,
                        response -> {
                            try {
                                final JSONObject o = new JSONObject(response);

                                if (!o.has("error")) {
                                    sharedPreferences.edit().putString("celsius",
                                            o.getString("celsius").concat("˚C")).apply();

                                    sharedPreferences.edit().putString("location",
                                            o.getString("location")).apply();

                                    weather.setText(o.getString("celsius").concat("˚C"));
                                    weatherBot.setText(o.getString("location"));
                                } else {
                                    Log.e("JSONException", "Response got: " + o.getString("error"));
                                    FirebaseCrash.report(new Exception(o.getString("error")));
                                }

                            } catch (JSONException e) {
                                Log.e("JSONException", "Response got: " + response, e);
                                FirebaseCrash.report(e);
                            }

                        },
                        error -> {
                            Log.e("StringRequest error", error.toString());
                            FirebaseCrash.report(error);
                        })
        );


        final TextView tasksDue = (TextView) view.findViewById(R.id.tasks_due);
        tasksDue.setText(String.valueOf(HabitListManager.getInstance(getContext()).getDueCount()));

        // Inflate the layout for this fragment
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        final TimeLineAdapter timeLineAdapter =
                new TimeLineAdapter(HabitListManager.getInstance(getContext()).getHabitsList());
        timeLineAdapter.setHasStableIds(true);
        recyclerView.setAdapter(timeLineAdapter);
        initDaysTabs(tabLayout, timeLineAdapter);
        return view;

    }

    private void initDaysTabs(final TabLayout tabLayout, final TimeLineAdapter timeLineAdapter) {
        if (tabLayout != null) {
            final String[] daysOfWeek =
                    {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            Log.i("HomeFragment", "dayOfWeek = " + daysOfWeek[dayOfWeek]);
            for (int i = 0; i < 7; i++) {
                tabLayout.addTab(tabLayout.newTab().setText(daysOfWeek[(dayOfWeek + i) % 7]));
            }

            tabLayout.setSmoothScrollingEnabled(true);
            tabLayout.setScrollPosition(0, 0f, true);
            final TabLayout.Tab tab = tabLayout.getTabAt(0);

            if (tab != null) {
                tab.select();
            }

            final HomeDayManager homeDayManager =
                    new HomeDayManager(HabitListManager.getInstance(getContext()).getHabitsList());

            tabLayout.addOnTabSelectedListener(
                    new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            if (tab.getPosition() != 0) {
                                int day = (dayOfWeek + tab.getPosition()) % 7;
                                Log.i("HomeFragment", "Day = " + day);
                                switch (day) {
                                    case 0:
                                        timeLineAdapter.updateList(homeDayManager.sun());
                                        break;
                                    case 1:
                                        timeLineAdapter.updateList(homeDayManager.mon());
                                        break;
                                    case 2:
                                        timeLineAdapter.updateList(homeDayManager.tue());
                                        break;
                                    case 3:
                                        timeLineAdapter.updateList(homeDayManager.wed());
                                        break;
                                    case 4:
                                        timeLineAdapter.updateList(homeDayManager.thu());
                                        break;
                                    case 5:
                                        timeLineAdapter.updateList(homeDayManager.fri());
                                        break;
                                    case 6:
                                        homeDayManager.updateListForSat(timeLineAdapter);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    }

            );
        }
    }
}