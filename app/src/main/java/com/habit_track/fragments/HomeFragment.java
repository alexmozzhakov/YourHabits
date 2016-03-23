package com.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView weatherBot = (TextView) view.findViewById(R.id.weatherBot);

        final TextView weather = (TextView) view.findViewById(R.id.weather);

        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.URL_WEATHER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("celsius", o.getString("celsius")).apply();
                            editor.putString("location", o.getString("location")).apply();
                            weather.setText(o.getString("celsius"));
                            final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
                            weatherBot.setTypeface(face);
                            weatherBot.setText(o.getString("location"));
                            //setBigAndSmallText(o.getString("celsius"), o.getString("location"), weather, new RelativeSizeSpan(3f), new RelativeSizeSpan(1.2f));
                        } catch (JSONException ignored) {

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                    }
                });


        rq.add(stringRequest);
        // Inflate the layout for this fragment
        return view;
    }
}