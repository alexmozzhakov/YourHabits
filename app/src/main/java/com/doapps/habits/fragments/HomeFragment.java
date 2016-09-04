package com.doapps.habits.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.adapter.TimeLineAdapter;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.HomeDayManager;
import com.doapps.habits.models.IDayManager;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitListProvider;
import com.doapps.habits.models.IStringSelector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView weather;
    private TextView weatherBot;
    @SuppressWarnings("HardCodedStringLiteral")
    private static final String URL_WEATHER_API = "http://habbitsapp.esy.es/weather.php";

    /**
     * @return true if user is connected to Internet
     */
    private static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.timeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        IHabitListProvider habitListManager =
                HabitListManager.getInstance(getContext());

        List<Habit> habitList = new ArrayList<>(habitListManager.getList());
        // filter list for today
        HomeDayManager.filterListForToday(habitList);

        // set due count of filtered list
        TextView tasksDue = (TextView) view.findViewById(R.id.tasks_due);
        tasksDue.setText(getDueCount(habitList));

        weather = (TextView) view.findViewById(R.id.weather);
        weatherBot = (TextView) view.findViewById(R.id.weatherBot);
        if (isConnected(getContext())) {
            getWeather(getContext(), habitList.size());
        } else {
            weather.setText(String.valueOf(habitList.size()));
            weatherBot.setText("All tasks");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager conMan = (ConnectivityManager)
                        getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                Log.i("HomeFragment", "addDefaultNetworkActiveListener");
                ConnectivityManager.OnNetworkActiveListener activeListener =
                        new ConnectivityManager.OnNetworkActiveListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onNetworkActive() {
                                getWeather(getContext(), habitList.size());

                                FirebaseUser[] user = {FirebaseAuth.getInstance().getCurrentUser()};
                                if (user[0] == null) {
                                    FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
                                        MainActivity ac = (MainActivity) getActivity();
                                        user[0] = FirebaseAuth.getInstance().getCurrentUser();
                                        ac.onSetupNavigationDrawer(user[0]);
                                    });
                                } else {
                                    ((MainActivity) getActivity()).onSetupNavigationDrawer(user[0]);
                                }
                                conMan.removeDefaultNetworkActiveListener(this);
                            }
                        };
                conMan.addDefaultNetworkActiveListener(activeListener);
            }
        }

        // set filtered list to adapter
        TimeLineAdapter timeLineAdapter = new TimeLineAdapter(habitList);
        timeLineAdapter.setHasStableIds(true);
        recyclerView.setAdapter(timeLineAdapter);

        // get day of week
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        IStringSelector swipeSelector = (IStringSelector) view.findViewById(R.id.sliding_tabs);
        swipeSelector.setItems(getDaysOfWeekFromToday(dayOfWeek - 1));

        initDaysTabs(
                dayOfWeek,
                swipeSelector,
                new HomeDayManager(timeLineAdapter,
                        HabitListManager.getInstance(getContext()).getList()));
        return view;

    }

    private String[] getDaysOfWeekFromToday(int dayOfWeek) {
        String[] daysOfWeekNames = getActivity().getResources().getStringArray(R.array.days_of_week_array);
        String[] daysOfWeek = new String[7];
        for (int i = 0; i < daysOfWeekNames.length; i++) {
            daysOfWeek[i] = daysOfWeekNames[(dayOfWeek + i) % 7];
        }
        return daysOfWeek;
    }

    /**
     * Initiates
     *
     * @param swipeStringSelector by using
     * @param habitDayManager     for filtering list
     * @param dayOfWeek           for getting dayOfWeek from number of selected item
     */
    private static void initDaysTabs(
            int dayOfWeek,
            IStringSelector swipeStringSelector,
            IDayManager habitDayManager) {

        swipeStringSelector.setOnItemSelectedListener(item -> {
            int value = swipeStringSelector.getAdapter().getCurrentPosition();
            if (value == 0) {
                habitDayManager.updateForToday();

                if (BuildConfig.DEBUG) {
                    Log.i("HomeFragment", "Selected day (today) = " + dayOfWeek);
                }

            } else {
                int day = (dayOfWeek + value) % 7;

                if (BuildConfig.DEBUG) {
                    Log.i("HomeFragment", "Selected day = " + day);
                }

                habitDayManager.updateListByDayOfWeek(day);
            }
        });
    }


    /**
     * @param habitsList is iterable list of {@link Habit}
     * @return String value of number of incomplete habits
     */
    private static CharSequence getDueCount(Iterable<Habit> habitsList) {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (Habit habit : habitsList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }
        return String.valueOf(counter);
    }

    private void getWeather(Context context, int listSize) {
        Volley.newRequestQueue(context.getApplicationContext()).add(
                new StringRequest(Request.Method.GET, URL_WEATHER_API,
                        response -> {
                            try {
                                JSONObject o = new JSONObject(response);

                                if (o.has("error")) {
                                    Log.e("JSONException", "Response got: " + o.getString("error"));
                                } else {
                                    if (getActivity() != null) {
                                        getActivity()
                                                .getSharedPreferences("pref", Context.MODE_PRIVATE)
                                                .edit()
                                                .putString("location", o.getString("location"))
                                                .apply();
                                    }

                                    weather.setText(o.getString("celsius") + "˚C");
                                    weatherBot.setText(o.getString("location"));
                                }

                            } catch (JSONException e) {
                                Log.e("JSONException", "Response got: " + response, e);
                                weather.setText(String.valueOf(listSize));
                                weatherBot.setText("All tasks");
                            }

                        },
                        error -> {
                            Log.e("StringRequest error", error.toString());
                            weather.setText(String.valueOf(listSize));
                            weatherBot.setText("All tasks");
                        })
        );
    }
}