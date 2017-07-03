package com.doapps.habits.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.doapps.habits.database.HabitsDatabase;
import com.doapps.habits.helper.ConnectionManager;
import com.doapps.habits.helper.ConnectionReceiver;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.helper.HomeDayManager;
import com.doapps.habits.listeners.WeatherNetworkStateListener;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IDayManager;
import com.doapps.habits.models.IStringSelector;
import com.doapps.habits.models.IWeatherUpdater;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment implements IWeatherUpdater {
    private static final String URL_WEATHER_API = "http://habit.esy.es/weather.php";
    private static final String BROADCAST = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static HabitsDatabase habitsDatabase;
    private TextView weather;
    private TextView weatherBot;
    private int habitListSize;
    private ConnectionReceiver connectionReceiver;

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
                    Log.i(TAG, "Selected day (today) = " + dayOfWeek);
                }

            } else {
                int daysFromWeekStart = dayOfWeek + value;
                int day = daysFromWeekStart > 7 ? daysFromWeekStart % 7 : daysFromWeekStart;

                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Selected day = " + day);
                }

                habitDayManager.updateListByDayOfWeek(day);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        toolbar.setTitle(R.string.home);

        weather = view.findViewById(R.id.weather);
        weatherBot = view.findViewById(R.id.weatherBot);

        habitsDatabase = HabitListManager.getInstance(getContext()).getDatabase();
        new GetTask(view).execute();

        if (ConnectionManager.isConnected(getContext())) {
            getWeather();
        } else {
            weather.setText(String.valueOf(habitListSize));
            weatherBot.setText(R.string.all_tasks);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager conMan = (ConnectivityManager)
                        getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                ConnectivityManager.OnNetworkActiveListener activeListener =
                        new ConnectivityManager.OnNetworkActiveListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onNetworkActive() {
                                Log.i("HF", "onNetworkActive");
                                getWeather();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user == null) {
                                    FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
                                        MainActivity ac = (MainActivity) getActivity();
                                        ac.onSetupNavigationDrawer(
                                                FirebaseAuth.getInstance().getCurrentUser());
                                    });
                                } else {
                                    ((MainActivity) getActivity()).onSetupNavigationDrawer(user);
                                }
                                conMan.removeDefaultNetworkActiveListener(this);
                            }

                        };
                conMan.addDefaultNetworkActiveListener(activeListener);
                Log.i(TAG, "addDefaultNetworkActiveListener");
            } else {
                WeatherNetworkStateListener weatherNetworkStateListener
                        = new WeatherNetworkStateListener(this, getActivity());
                connectionReceiver = new ConnectionReceiver(weatherNetworkStateListener);

                IntentFilter intentFilter = new IntentFilter(BROADCAST);
                getActivity().registerReceiver(connectionReceiver, intentFilter);
                Log.i(TAG, "addDefaultNetworkActiveListener");
            }
        }
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

    @SuppressLint("SetTextI18n")
    public void getWeather() {
        Volley.newRequestQueue(getContext().getApplicationContext()).add(
                new StringRequest(Request.Method.GET, URL_WEATHER_API,
                        response -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                if (jsonResponse.has("error")) {
                                    onNetworkFail(
                                            new Exception(jsonResponse.getString("error")), habitListSize);
                                } else {
                                    if (getActivity() != null) {
                                        getActivity()
                                                .getSharedPreferences("pref", Context.MODE_PRIVATE)
                                                .edit()
                                                .putString("location", jsonResponse.getString("location"))
                                                .apply();
                                    }

                                    weather.setText(jsonResponse.getString("celsius") + "˚C");
                                    weatherBot.setText(jsonResponse.getString("location"));
                                }

                            } catch (JSONException error) {
                                onNetworkFail(error, habitListSize);
                            }

                        },
                        error -> onNetworkFail(error, habitListSize))
        );
    }

    private void onNetworkFail(Exception error, int listSize) {
        if (getActivity() == null) return;
        Log.e("StringRequest error", error.toString());
        weather.setText(String.valueOf(listSize));
        weatherBot.setText(R.string.all_tasks);
        ((MainActivity) getActivity()).removeAvatarPadding();
    }

    @Override
    public void onDestroy() {
        if (connectionReceiver != null)
            getActivity().unregisterReceiver(connectionReceiver);
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetTask extends AsyncTask<Void, Void, List<Habit>> {
        private final View view;

        GetTask(View view) {
            this.view = view;
        }

        @Override
        protected List<Habit> doInBackground(Void... voids) {
            return habitsDatabase.habitDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Habit> habits) {
            super.onPostExecute(habits);

            // set filtered list to adapter
            TimeLineAdapter timeLineAdapter = new TimeLineAdapter(habits);
            timeLineAdapter.setHasStableIds(true);
            RecyclerView recyclerView = view.findViewById(R.id.timeline);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(timeLineAdapter);

            // filter list for today
            HomeDayManager dayManager = new HomeDayManager(timeLineAdapter, habits);
            dayManager.updateForToday();

            // set due count of filtered list
            TextView tasksDue = view.findViewById(R.id.tasks_due_num);
            tasksDue.setText(dayManager.getDueCount());

            // get list size
            habitListSize = habits.size();

            // get day of week
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            IStringSelector swipeSelector = view.findViewById(R.id.sliding_tabs);
            swipeSelector.setItems(getDaysOfWeekFromToday(dayOfWeek - 1));

            initDaysTabs(
                    dayOfWeek,
                    swipeSelector,
                    new HomeDayManager(timeLineAdapter,
                            habits));

        }
    }
}