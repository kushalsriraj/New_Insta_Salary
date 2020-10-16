package rutherfordit.com.instasalary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class LoginActivity extends AppCompatActivity implements OTPListener {

    CardView progressBar;
    SmsVerifyCatcher smsVerifyCatcher;
    LinearLayout llfirst, llsecond;
    TextInputEditText enterphoneno_login;
    RelativeLayout loginmainlayout;
    String Status, loginotp, enteredotp;
    String substring_phoneno;
    SharedPreferences sharedpreferences;
    String mypreference = "mySharedPreference";
    TextView mynumbertext;
    boolean click = false;
    private RelativeLayout loginbottombutton;
    EditText et1;
    EditText et2;
    EditText et3;
    EditText et4;
    private TextView changenumberlogin;
    private Snackbar snackbar;
    // int click = 0;
    String dgt1,dgt2,dgt3,dgt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        OtpReader.bind(this,"QP-600010");


        progressBar = findViewById(R.id.loader_login);
        progressBar.setVisibility(View.GONE);
        loginmainlayout = findViewById(R.id.loginmainlayout);


        // progressBar = new ProgressDialog(this);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);

        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");


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

        smsVerifyCatcher = new SmsVerifyCatcher(LoginActivity.this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                Log.d("Agilanbu OTP", code);
                Toast.makeText(getApplicationContext(), "Agilanbu OTP: " + code, Toast.LENGTH_LONG).show();
               // et_otp.setText(code);//set code in edit text
            }
        });

        smsVerifyCatcher.setFilter("QP-600010");

        ontouch();
    }

    private String parseCode(String message) {

        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        //init();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
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

                if (et4.getText().toString().length() == 1 && et3.getText().toString().length() == 1 && et2.getText().toString().length() == 1 && et1.getText().toString().length() == 1)     //size as per your requirement
                {
                    enteredotp = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();

                    if (Status.equals("true")) {

                        if (loginotp.equals(enteredotp)) {

                            progressBar.setVisibility(View.VISIBLE);

                            generateToken();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Entered OTP is Wrong", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        SignupRequest();
                    }

                }/*
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Enter 4 number OTP..",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }*/
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

        et3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (et3.getText().toString().length() == 0) {
                        et2.requestFocus();
                    }
                }
                return false;
            }
        });
        et2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (et2.getText().toString().length() == 0) {
                        et1.requestFocus();
                    }
                }
                return false;
            }
        });
        et4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (et4.getText().toString().length() == 0) {
                        et3.requestFocus();
                    }
                }
                return false;
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
                progressBar.setVisibility(View.GONE);
                snackbar.show();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            llfirst.setVisibility(View.VISIBLE);
            llsecond.setVisibility(View.GONE);
        }
    }

    private void requestlogin() {

        progressBar.setVisibility(View.VISIBLE);

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
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(),"Hi Looks Like You Are A New User. Please Enter OTP and Fill Your Details !!",Toast.LENGTH_LONG).show();
                    } else {
                        llfirst.setVisibility(View.GONE);
                        llsecond.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
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
                        editor.putString("Phone", substring_phoneno);
                        editor.putString("loggedin", "true");
                        editor.apply();

                        sendlocation(accesstoken, "login");

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

    private void sendlocation(String accesstoken, String from) {

        String lat = sharedpreferences.getString("lat", "");
        String longi = sharedpreferences.getString("longi", "");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", longi);
            jsonObject.put("latitude", lat);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("latlong", "sendlocation: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.LOCATION_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("latlong", "onResponse: " + response);

                if (response.has("message")) {
                    try {

                        if (from.equals("login")) {
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            et1.setText("");
                            et2.setText("");
                            et3.setText("");
                            et4.setText("");
                        } else if (from.equals("signup")) {
                            Intent i = new Intent(getApplicationContext(), RequestPermissionsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            et1.setText("");
                            et2.setText("");
                            et3.setText("");
                            et4.setText("");
                            startActivity(i);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toast.makeText(getApplicationContext(), "422 error code", Toast.LENGTH_SHORT).show();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + accesstoken);
                Log.e("latlong", "getHeaders: " + params);
                return params;
            }
        };

        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void SignupRequest() {

        progressBar.setVisibility(View.VISIBLE);

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

                    progressBar.setVisibility(View.GONE);

                    JSONObject dataobj = response.getJSONObject("data");

                    JSONObject token = dataobj.getJSONObject("token");

                    String accesstoken = token.getString("access_token");

                    if (!accesstoken.isEmpty()) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("AccessToken", accesstoken);
                        editor.putString("Phone", substring_phoneno);
                        editor.putString("loggedin", "false");
                        editor.apply();

                        sendlocation(accesstoken, "signup");


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

    @Override
    public void otpReceived(String messageText) {

        String number  = messageText.replaceAll("[^0-9]", "");

        Toast.makeText(getApplicationContext(),"" + number.charAt(0),Toast.LENGTH_SHORT).show();

        dgt1 = String.valueOf(number.charAt(0));
        dgt2 = String.valueOf(number.charAt(1));
        dgt3 = String.valueOf(number.charAt(2));
        dgt4 = String.valueOf(number.charAt(3));

        et1.setText(dgt1);
        et2.setText(dgt2);
        et3.setText(dgt3);
        et4.setText(dgt4);

        Log.e("TAG", "otpReceived: " + dgt1 );

        /*et1.setText(number.charAt(0));
        et2.setText(number.charAt(1));
        et3.setText(number.charAt(2));
        et4.setText(number.charAt(3));*/

    }
}
