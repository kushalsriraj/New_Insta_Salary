package com.huygenslabs.instasalarystaging.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpDetails extends AppCompatActivity {

    RelativeLayout  pansubmit;
    TextInputEditText enterpan;
    ImageView backarrowsignupdetails;
    SharedPreferences sharedPreferences;
    int value;
    String  Panno, UserAccessToken;
    boolean click = false;
    ProgressDialog progressDialog;
    String Adharno;

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Action Denied..",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);

        init();

    }

    private void init() {

        Adharno = getIntent().getStringExtra("adharno");

        progressDialog = new ProgressDialog(this);
        pansubmit = findViewById(R.id.pansubmit);
        enterpan = findViewById(R.id.enterpan);

        enterpan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);

        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        backarrowsignupdetails = findViewById(R.id.backarrowsignupdetails);

        onclicks();

    }

    private void onclicks() {

        backarrowsignupdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


        enterpan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

                Matcher matcher = pattern.matcher(s);

                if (!TextUtils.isEmpty(enterpan.getText().toString()) && enterpan.getText().toString().trim().length() == 10 && matcher.matches())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pansubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                            click = true;
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pansubmit.setBackgroundColor(Color.parseColor("#36000000"));
                            click = false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pansubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click) {

                    Panno = enterpan.getText().toString();

                    progressDialog.setTitle("Saving Details..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    validatepan();


                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid PAN number", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void validatepan()
    {

        final JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("pan", Panno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "EnterPersonalDetails: " + jsonObject );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.PAN_VALIDATION, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                 //   JSONObject dataobj = response.getJSONObject("data");
                    sendpandata();
                    EnterPersonalDetails();
                    Toast.makeText(getApplicationContext(),response.getString("success"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                int code = error.networkResponse.statusCode;
                if (code == 422)
                {
                    Toast.makeText(getApplicationContext(),"Enter correct pan number",Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("API-KEY", "AKa4ea7236-02ed-42e2-b9aa-aed24ab8e36f");
                return params;

            }
        };

        MySingleton.getInstance(SignUpDetails.this).addToRequestQueue(jsonObjectRequest);


    }

    private void sendpandata()
    {

        final JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", sharedPreferences.getString("full_name",""));
            jsonObject.put("pan_number", enterpan.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "EnterPersonalDetails: " + jsonObject );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_PAN_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject dataobj = response.getJSONObject("data");

                    Log.e("data", "obj: " + dataobj);

                    if (dataobj.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Data Is Not Saved", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    } else {
                        Toast.makeText(getApplicationContext(), "Backend", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                return params;

            }
        };

        MySingleton.getInstance(SignUpDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    private void EnterPersonalDetails() {

        final JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("aadhar_number", Adharno);
            jsonObject.put("pan_number", Panno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "EnterPersonalDetails: " + jsonObject );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.PERSONAL_DETAILS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject dataobj = response.getJSONObject("data");

                    Log.e("data", "obj: " + dataobj);

                    if (dataobj.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Data Is Not Saved", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),SegmentActivity.class);
                        startActivity(intent);
                        progressDialog.cancel();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                return params;

            }
        };

        MySingleton.getInstance(SignUpDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    public void gotoPanupload(View view) {

        Intent i = new Intent(getApplicationContext(), PanImageUpload.class);
        startActivity(i);

    }

}
