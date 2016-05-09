package com.habit_track.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

public class AppController extends Application {

    // Server user login url
    public static final String URL_LOGIN = "http://habbitsapp.esy.es/android_login_api/login.php";
    // Server user register url
    public static final String URL_REGISTER = "http://habbitsapp.esy.es/android_login_api/register.php";
    // Server weather api
    public static final String URL_WEATHER_API = "http://habbitsapp.esy.es/weather_api.php";
    //Server programs api
    public static final String URL_PROGRAMS_API = "https://fiery-torch-2910.firebaseio.com/";

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private RequestQueue mRequestQueue;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Firebase.setAndroidContext(this);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(final Request<T> req, final String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

}