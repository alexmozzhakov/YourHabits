package com.habit_track.fragments;

import android.app.Fragment;
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

public class ProgramsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_programs, container, false);
        RequestQueue rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        final TextView txt = (TextView) result.findViewById(R.id.text);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.URL_PROGRAMS_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        txt.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                    }
                });


        rq.add(stringRequest);

        rq.add(stringRequest);

        return result;
    }
}
