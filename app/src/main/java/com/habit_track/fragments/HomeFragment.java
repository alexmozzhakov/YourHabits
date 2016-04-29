package com.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import com.habit_track.app.AppController;
import com.habit_track.app.Habit;
import com.habit_track.helper.HabitDBHandler;
import com.habit_track.helper.TimeLineAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView weather = (TextView) view.findViewById(R.id.weather);
        final TextView weatherBot = (TextView) view.findViewById(R.id.weatherBot);

        final TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);

        if (mTabLayout != null) {
            for (int i = 0; i < 7; i++) {
                mTabLayout.addTab(mTabLayout.newTab().setText("Tab " + i));
            }
        }

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            weather.setText(sharedPreferences.getString("celsius", "18"));
            weatherBot.setText(sharedPreferences.getString("location", "Error"));
        }
        
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(
                new StringRequest(Request.Method.GET, AppController.URL_WEATHER_API,
                        response -> {
                            try {
                                JSONObject o = new JSONObject(response);
                                final SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("celsius", o.getString("celsius").concat("˚C")).apply();
                                editor.putString("location", o.getString("location")).apply();

                                weather.setText(o.getString("celsius") + "˚C");

                                if (this.getActivity() != null) {
                                    Typeface face = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
                                    weatherBot.setTypeface(face);
                                }
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

        final List<Habit> mHabitList = ListFragment.mHabitsDatabase.getHabitDetailsAsArrayList();
        final Calendar mCalendar = Calendar.getInstance();
        final int date = mCalendar.get(Calendar.DATE);
        final int month = mCalendar.get(Calendar.MONTH);
        final int year = mCalendar.get(Calendar.YEAR);

        int counter = 0;
        for (final Habit habit : mHabitList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }

        final TextView mTasksDue = (TextView) view.findViewById(R.id.tasks_due);
        mTasksDue.setText(String.valueOf(counter));


        // Inflate the layout for this fragment
        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        final TimeLineAdapter mTimeLineAdapter = new TimeLineAdapter(mHabitList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        return view;

    }

}