package com.huygenslabs.instasalarystaging.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.huygenslabs.instasalarystaging.interfaces.SmsListener;
import com.huygenslabs.instasalarystaging.model.SMSData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog progressBar;
    LinearLayout llfirst, llsecond;
    TextInputEditText enterphoneno_login;
    ConstraintLayout loginmainlayout;
    String Status, loginotp, enteredotp;
    String substring_phoneno;
    SharedPreferences sharedpreferences;
    String mypreference = "mySharedPreference";
    TextView mynumbertext;
    private RelativeLayout loginbottombutton;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private TextView changenumberlogin;
    private Snackbar snackbar;
    boolean click = false;
   // int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        progressBar = new ProgressDialog(this);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);

        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");

        loginmainlayout = findViewById(R.id.loginmainlayout);
        changenumberlogin = findViewById(R.id.changenumberlogin);
        enterphoneno_login = findViewById(R.id.enterphoneno_login);
        loginbottombutton = findViewById(R.id.loginbottombutton);
        llfirst = findViewById(R.id.llfirst);
        llsecond = findViewById(R.id.llsecond);

        mynumbertext = findViewById(R.id.mynumbertext);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        llfirst.setVisibility(View.VISIBLE);
        llsecond.setVisibility(View.GONE);

        enterphoneno_login.setText("+91 ");

        snackbar = Snackbar.make(loginmainlayout, "Press Again To Exit", Snackbar.LENGTH_SHORT);

        ontouch();
    }

    @Override
    public void onResume() {
        //init();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void ontouch() {

        enterphoneno_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (enterphoneno_login.getText().toString().trim().length() == 14) {
                        loginbottombutton.setBackgroundColor(Color.parseColor("#D81B60"));
                        click = true;
                    } else {
                        loginbottombutton.setBackgroundColor(Color.parseColor("#36000000"));
                        click = false;
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {

                String prefix = "+91 ";
                int count = (s == null) ? 0 : s.toString().length();
                if (count < prefix.length()) {
                    enterphoneno_login.setText(prefix);
                    int selectionIndex = Math.max(count + 1, prefix.length());
                    enterphoneno_login.setSelection(selectionIndex);
                }
            }
        });

        et1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et1.getText().toString().length() == 1)     //size as per your requirement
                {
                    et2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        et2.addTextChangedListener(new TextWatcher() {

            boolean isBackspaceClicked;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et2.getText().toString().length() == 1)     //size as per your requirement
                {
                    et3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                isBackspaceClicked = after < count;

            }

            public void afterTextChanged(Editable s) {
                if (isBackspaceClicked) {
                    et1.requestFocus();
                }
            }
        });

        et3.addTextChangedListener(new TextWatcher() {

            boolean isBackspaceClicked;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et3.getText().toString().length() == 1)     //size as per your requirement
                {
                    et4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                isBackspaceClicked = after < count;

            }

            public void afterTextChanged(Editable s) {
                if (isBackspaceClicked) {
                    et2.requestFocus();
                }

            }
        });

        et4.addTextChangedListener(new TextWatcher() {

            boolean isBackspaceClicked;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et4.getText().toString().length() == 1)     //size as per your requirement
                {
                    enteredotp = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();

                    if (Status.equals("true")) {
                        if (loginotp.equals(enteredotp)) {

                            progressBar.setCancelable(true);
                            progressBar.setMessage("OTP Validating...");
                            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressBar.setProgress(0);
                            progressBar.setMax(100);
                            progressBar.show();

                            generateToken();

                        } else {
                            progressBar.cancel();
                            Toast.makeText(getApplicationContext(), "Entered OTP is Wrong", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        SignupRequest();
                    }

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                isBackspaceClicked = after < count;
            }

            public void afterTextChanged(Editable s) {
                if (isBackspaceClicked) {
                    et3.requestFocus();
                }

            }
        });

        loginbottombutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click) {
                    requestlogin();
                    et1.requestFocus();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid phone number", Toast.LENGTH_LONG).show();
                }
            }
        });

        changenumberlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llfirst.setVisibility(View.VISIBLE);
                llsecond.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (llfirst.getVisibility() == View.VISIBLE) {
            if (snackbar.isShown()) {
                super.onBackPressed();
            } else {
                snackbar.show();
            }
        } else {
            llfirst.setVisibility(View.VISIBLE);
            llsecond.setVisibility(View.GONE);
        }
    }

    private void requestlogin() {

        progressBar.setCancelable(true);
        progressBar.setMessage("Loading...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
        progressBar.show();

        JSONObject jsonObject = new JSONObject();

        String number = enterphoneno_login.getText().toString();

        substring_phoneno = number.substring(3);

        try {

            jsonObject.put("phone_number", substring_phoneno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "requestlogin: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_OTP_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    Status = response.getString("isuserExits");
                    loginotp = response.getString("otp");

                    Toast.makeText(getApplicationContext(), loginotp, Toast.LENGTH_LONG).show();

                    mynumbertext.setText(enterphoneno_login.getText().toString());

                    if (Status.equals("false")) {
                        llfirst.setVisibility(View.GONE);
                        llsecond.setVisibility(View.VISIBLE);
                        progressBar.cancel();
                        //Toast.makeText(getApplicationContext(),"Hi Looks Like You Are A New User. Please Enter OTP and Fill Your Details !!",Toast.LENGTH_LONG).show();
                    } else {
                        llfirst.setVisibility(View.GONE);
                        llsecond.setVisibility(View.VISIBLE);
                        progressBar.cancel();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", "onErrorResponse: " + error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };

        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void generateToken() {
        JSONObject jsonObject = new JSONObject();

        String number = enterphoneno_login.getText().toString();

        substring_phoneno = number.substring(3);

        try {

            jsonObject.put("grant_type", "password");
            jsonObject.put("client_id", "4");
            jsonObject.put("client_secret", Urls.Client_Secret_key);
            jsonObject.put("username", substring_phoneno);
            jsonObject.put("password", enteredotp);
            jsonObject.put("scope", "*");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "requestlogin: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.GENERATE_TOKEN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    String accesstoken = response.getString("access_token");

                    if (!accesstoken.isEmpty()) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("AccessToken", accesstoken);
                        editor.putString("Phone",substring_phoneno);
                        editor.putString("loggedin","true");
                        editor.apply();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        et1.setText("");
                        et2.setText("");
                        et3.setText("");
                        et4.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(), "Access Token Not Generated", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", "onErrorResponse: " + error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };

        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void SignupRequest() {

        progressBar.setCancelable(true);
        progressBar.setMessage("OTP Validating...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("phone_number", substring_phoneno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SIGNUP_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    progressBar.cancel();

                    JSONObject dataobj = response.getJSONObject("data");

                    JSONObject token = dataobj.getJSONObject("token");

                    String accesstoken = token.getString("access_token");

                    if (!accesstoken.isEmpty()) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("AccessToken", accesstoken);
                        editor.putString("Phone",substring_phoneno);
                        editor.putString("loggedin","false");
                        editor.apply();

                        Intent i = new Intent(getApplicationContext(), TakeSelfieActivity.class);
                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(), "Access Token Is Not Generated!!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error", "onErrorResponse: " + error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return params;
            }
        };

        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
    }

}
