/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.habit_track.habit_track.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.habit_track.R;
import com.habit_track.habit_track.app.AppController;
import com.habit_track.habit_track.helper.SQLiteHandler;
import com.habit_track.habit_track.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z \\-\\.']*$");
    // protected static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String TAG_STRING = "req_register";
    private ProgressDialog pDialog;
    private SQLiteHandler mDatabase;

    public static boolean isValidPattern(final String str, final Pattern pattern) {
        return pattern.matcher(str).matches();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText mInputFullName = (EditText) findViewById(R.id.fullname);
        final EditText mInputEmail = (EditText) findViewById(R.id.email);
        final EditText mInputPassword = (EditText) findViewById(R.id.password);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        final SessionManager session = new SessionManager(getApplicationContext());

        // SQLite database handler
        mDatabase = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            final Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(view -> {
            String name = mInputFullName.getText().toString().trim();
            final String email = mInputEmail.getText().toString().trim();
            final String password = mInputPassword.getText().toString().trim();
            if (name.isEmpty()) {
                name = "Anonymous";
            }
            if (!email.isEmpty() && !password.isEmpty()) {
                if (isValidPattern(name, NAME_PATTERN)) {
                    if (isValidPattern(email, EMAIL_PATTERN)) {
                        registerUser(name, email, password);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter your details!", Toast.LENGTH_LONG)
                        .show();
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(view -> {
            final Intent intent = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String email,
                              final String password) {
        // Tag used to cancel the request

        pDialog.setMessage("Registering ...");
        showDialog();

        final StringRequest strReq = new StringRequest(Method.POST,
                AppController.URL_REGISTER, (Response.Listener<String>) response -> {
            Log.d(TAG, "Register Response: " + response);
            hideDialog();

            try {
                final JSONObject jObj = new JSONObject(response);
                final boolean error = jObj.getBoolean("error");
                if (!error) {
                    // User successfully stored in MySQL
                    // Now store the user in sqlite
                    //String id = jObj.getString("id");

                    JSONObject user = jObj.getJSONObject("user");

                    // Inserting row in users table
                    mDatabase.addUser(user.getString("name"),
                            user.getString("email"), user.getString("created_at"));

                    Toast.makeText(getApplicationContext(),
                            "User successfully registered. Try login now!",
                            Toast.LENGTH_LONG).show();

                    // Launch login activity
                    final Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error occurred in registration.
                    // Get the error message

                    final String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("JSONException", "response error", e);
            }

        }, (Response.ErrorListener) error -> {
            Log.e(TAG, "Registration Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                final Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, TAG_STRING);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
