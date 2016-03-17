package com.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
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

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences sharedpreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView txt = (TextView) view.findViewById(R.id.weather);

        if (sharedpreferences.contains("celsius")) {
            setBigAndSmallText(sharedpreferences.getString("celsius", "Loading..."), sharedpreferences.getString("location", "Loading..."), txt, new RelativeSizeSpan(3f), new RelativeSizeSpan(1.2f));
        }

        RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.URL_WEATHER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("celsius", o.getString("celsius")).apply();
                            editor.putString("location", o.getString("location")).apply();
                            setBigAndSmallText(o.getString("celsius"), o.getString("location"), txt, new RelativeSizeSpan(3f), new RelativeSizeSpan(1.2f));
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

    public static void setBigAndSmallText(String big, String small, TextView text, RelativeSizeSpan bigSize, RelativeSizeSpan smallSize) {
        SpannableString bigSpan = new SpannableString(big);
        SpannableString smallSpan = new SpannableString(small);
        bigSpan.setSpan(bigSize, 0, bigSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        smallSpan.setSpan(smallSize, 0, smallSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(TextUtils.concat(bigSpan, "\n", smallSpan));
    }
}