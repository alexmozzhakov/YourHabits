package com.habit_track.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.habit_track.R;
import com.habit_track.app.AppController;
import com.habit_track.helper.SQLiteHandler;
import com.habit_track.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextInputEditText inputEmail = (TextInputEditText) findViewById(R.id.email);
        final TextInputEditText inputPassword = (TextInputEditText) findViewById(R.id.password);
        final Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        inputEmail.setTypeface(face);
        inputPassword.setTypeface(face);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        inputPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        inputPassword.setOnKeyListener((final View v, final int keyCode, final KeyEvent event) -> {
            // If the event is a key-down event on the "enter" button
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();

                }
                return true;
            }
            return false;
        });

        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            final String email = inputEmail.getText().toString().trim();
            final String password = inputPassword.getText().toString().trim();

            // Check for empty data in the form
            if (!email.isEmpty() && !password.isEmpty()) {
                // login user
                checkLogin(email, password);
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(),
                        "Please enter the credentials!", Toast.LENGTH_LONG)
                        .show();
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            final Intent i = new Intent(getApplicationContext(),
                    RegisterActivity.class);
            startActivity(i);
            finish();
        });

    }

    /**
     * function to verify login details in mysql db
     */
    protected void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        showDialog();

        final StringRequest strReq = new StringRequest(Method.POST,
                AppController.URL_LOGIN, (Response.Listener<String>) response -> {
            Log.d(TAG, "Login Response: " + response);
            hideDialog();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    session.setLogin(true);

                    final JSONObject user = jObj.getJSONObject("user");
                    final String name = user.getString("name");
                    final String email1 = user.getString("email");
                    final String created_at = user
                            .getString("created_at");

                    // Inserting row in users table
                    db.addUser(name, email1, created_at);

                    // Launch main activity
                    final Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, (Response.ErrorListener) error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                final Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void forgotPass(final View view) {

    }
}
