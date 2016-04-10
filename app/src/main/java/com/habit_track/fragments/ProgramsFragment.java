package com.habit_track.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.habit_track.R;
import com.habit_track.activity.MainActivity;
import com.habit_track.app.AppController;
import com.habit_track.app.Program;
import com.habit_track.helper.ProgramListAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProgramsFragment extends Fragment {
    private JSONObject jsonObject;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_programs, container, false);

        //Create array of programs
        final Program program_data[] = new Program[3];

        //Create request
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, AppController.URL_PROGRAMS_API,
                response -> {
                    try {
                        // GET Json response from programs db
                        jsonObject = new JSONObject(response);

                        for (int i = 0; i < 4; i++) {

                            //Get program as json object from request
                            final JSONObject program = jsonObject.getJSONObject(String.valueOf(i + 1));

                            //Styles for top program
                            if (i == 0) {
                                ImageView bestImage = (ImageView) result.findViewById(R.id.imageView1);

                                //Create link for image to download from json request
                                final String imageLink = "http://habbitsapp.esy.es/img_progs/"
                                        + program.getString("image");

                                Log.i("Image request", imageLink);

                                //Download image async and set to ImageView
                                Picasso.with(getActivity().getApplicationContext())
                                        .load(imageLink)
                                        .into(bestImage);


                                //Load typefaces from assets
                                final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Regular.ttf");
                                final Typeface faceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Light.otf");

                                //Style title
                                final TextView titleTop = ((TextView) result.findViewById(R.id.titleTop));
                                titleTop.setText(program.getString("name"));
                                titleTop.setTextColor(Color.WHITE);
                                titleTop.setTypeface(faceLight);
                                titleTop.setOnClickListener(this::top);

                                //Style percent view
                                TextView successTop = (TextView) result.findViewById(R.id.percentTop);
                                successTop.setText(program.getString("success") + "%");
                                successTop.setTypeface(face);

                            }
                            // Add other programs to array
                            else {
                                program_data[i - 1] = new Program(program.getString("name"), program.getString("success") + "% SUCCEEDED");
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", "response error", e);
                    } finally {
                        //Use adapter for array of non-top programs
                        if (program_data[0] != null) {
                            final ProgramListAdapter adapter = new ProgramListAdapter(getActivity(),
                                    R.layout.prog_listitem, program_data);
                            final ListView listView = (ListView) result.findViewById(R.id.mainListView);
                            listView.setAdapter(adapter);
                        }

                    }
                }, error -> {
        });

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);

        return result;
    }
    public static FragmentManager fm;
    private static boolean isShowing;

    public void top(final View view) {
        if(!isShowing) {
            try {
                final Bundle bundle = new Bundle();
                final JSONObject program = jsonObject.getJSONObject("1");
                bundle.putString("title", program.getString("name"));
                bundle.putString("description", program.getString("description"));

                MainActivity.lastFragment = new ProgramFragment();
                MainActivity.lastFragment.setArguments(bundle);
                MainActivity.navigationView.getMenu().getItem(1).setChecked(false);

                fm = getFragmentManager();
                fm.beginTransaction().add(R.id.content_frame, MainActivity.lastFragment).commit();

            } catch (JSONException e) {
                Log.e("JSONException", "response error", e);
            }
            isShowing = true;
        } else {
            fm.beginTransaction().remove(MainActivity.lastFragment).commit();
            isShowing = false;
        }
    }
}