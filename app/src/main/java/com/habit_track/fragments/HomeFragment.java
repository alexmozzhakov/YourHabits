package com.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.habit_track.R;
import com.habit_track.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView weatherBot = (TextView) view.findViewById(R.id.weatherBot);

        final TextView weather = (TextView) view.findViewById(R.id.weather);

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        final RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Tab One"));
            tabLayout.addTab(tabLayout.newTab().setText("Tab Two"));
            tabLayout.addTab(tabLayout.newTab().setText("Tab Three"));
        }


        final StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.URL_WEATHER_API,
                response -> {
                    try {
                        JSONObject o = new JSONObject(response);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("celsius", o.getString("celsius")).apply();
                        editor.putString("location", o.getString("location")).apply();

                        weather.setText(o.getString("celsius") + "˚C");

                        if (this.getActivity() != null) {
                            Typeface face = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
                            weatherBot.setTypeface(face);
                        }
                        weatherBot.setText(o.getString("location"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                });


        rq.add(stringRequest);
        // Inflate the layout for this fragment
        return view;
    }
}