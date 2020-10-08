package com.huygenslabs.instasalarystaging.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetAdharDetailsActivity extends AppCompatActivity {

    private static final String TAG = "adhar" ;
    TextInputEditText enteradhar;
    RelativeLayout Adharsubmit;
    SharedPreferences sharedPreferences;
    String UserAccessToken,Client_id;
    BottomSheetDialog bottomSheetDialog;
    View view;
    ProgressDialog progressDialog;
    boolean click = false;
    Button submitadharotp;
    EditText enteradharotp;
    ProgressBar progressBar;
    LinearLayout otp_layout;
    String mypreference = "mySharedPreference";
    String client_id,full_name,aadhar_number,dob,gender,country,dist,state,po,ioc,vtc,subdist,street,house,landmark,zip,has_image,care_of,moblie_verified;
    String profile_image = "";
    JSONObject data,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_adhar_details);

        init();
    }

    private void init() {

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        enteradhar = findViewById(R.id.enteradharNumber);
        Adharsubmit = findViewById(R.id.AdharsubmitButton);

        view = getLayoutInflater().inflate(R.layout.adhar_bottom_sheet, null);
        progressBar = view.findViewById(R.id.progress);
        otp_layout = view.findViewById(R.id.otp_layout);
        enteradharotp = view.findViewById(R.id.enteradharotp);
        submitadharotp = view.findViewById(R.id.submitadharotp);
        bottomSheetDialog = new BottomSheetDialog(GetAdharDetailsActivity.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);

        enteradhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(enteradhar.getText().toString()) && enteradhar.getText().toString().trim().length() == 12)
                {
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    validateAdhar();
                }
                else
                {
                    Adharsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    Adharsubmit.setEnabled(false);
                    click = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submitadharotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enteradharotp.getText().toString().length() > 5)
                {
                    progressBar.setVisibility(View.VISIBLE);
                   // otp_layout.setVisibility(View.GONE);
                    getadhardetails();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Enter 6 Digit OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void validateAdhar()
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("aadhaarNumber", enteradhar.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADHAR_VALIDATION, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getInt("status_code") == 200 )
                    {
                        Toast.makeText(getApplicationContext(),"Validation Successfull",Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.show();
                        sendotpadhar();
                        progressDialog.cancel();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Adhar Number Doesnot exist... Please Enter Correct Adhar Number",Toast.LENGTH_SHORT).show();
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
                params.put("API-KEY", "AKa4ea7236-02ed-42e2-b9aa-aed24ab8e36f");
                return params;

            }
        };

        MySingleton.getInstance(GetAdharDetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void getadhardetails()
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("clientId", Client_id);
            jsonObject.put("otp", enteradharotp.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADHAR_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    progressDialog.setTitle("Fetching Adhar data..");
                    progressDialog.show();

                    if (response.getInt("status_code") == 200 )
                    {

                        data = response.getJSONObject("data");
                        address = data.getJSONObject("address");

                      //  Log.e(TAG, "onResponse: " + address );

                        client_id = data.getString("client_id");
                        full_name = data.getString("full_name");
                        aadhar_number = data.getString("aadhaar_number");
                        dob = data.getString("dob");
                        gender = data.getString("gender");
                        country = address.getString("country");
                        dist = address.getString("dist");
                        state = address.getString("state");
                        po = address.getString("po");
                        ioc = address.getString("loc");
                        vtc = address.getString("vtc");
                        subdist = address.getString("subdist");
                        street = address.getString("street");
                        house = address.getString("house");
                        landmark = address.getString("landmark");
                        zip = data.getString("zip");
                      //  profile_image = data.getString("profile_image");
                        has_image = data.getString("has_image");
                        care_of = data.getString("care_of");
                        moblie_verified = data.getString("mobile_verified");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("client_id", data.getString("client_id"));
                        editor.putString("full_name",full_name);
                        editor.putString("aadhar_number", aadhar_number);
                        editor.putString("dob",dob);
                        editor.putString("gender", gender);
                        editor.putString("country",country);
                        editor.putString("dist", dist);
                        editor.putString("state",state);
                        editor.putString("po", po);
                        editor.putString("ioc",ioc);
                        editor.putString("vtc", vtc);
                        editor.putString("subdist",subdist);
                        editor.putString("street", street);
                        editor.putString("house",house);
                        editor.putString("landmark", landmark);
                        editor.putString("zip",zip);
                        editor.putString("profile_image",profile_image);
                        editor.putString("has_image",has_image);
                        editor.putString("care_of", care_of);
                        editor.putString("moblie_verified",moblie_verified);

                        editor.apply();


                        Adharsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        Adharsubmit.setEnabled(true);
                        click = true;
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(),"DATA RETREIVED",Toast.LENGTH_SHORT).show();
                        sendadhardata();

                    }
                    else
                    {
                        progressDialog.cancel();
                        Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
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
                params.put("API-KEY", "AKa4ea7236-02ed-42e2-b9aa-aed24ab8e36f");
                return params;

            }
        };

        MySingleton.getInstance(GetAdharDetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void sendadhardata()
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("client_id", client_id);
            jsonObject.put("full_name",full_name);
            jsonObject.put("aadhar_number", aadhar_number);
            jsonObject.put("dob",dob);
            jsonObject.put("gender", gender);
            jsonObject.put("country",country);
            jsonObject.put("dist", dist);
            jsonObject.put("state",state);
            jsonObject.put("po", po);
            jsonObject.put("ioc",ioc);
            jsonObject.put("vtc", vtc);
            jsonObject.put("subdist",subdist);
            jsonObject.put("street", street);
            jsonObject.put("house",house);
            jsonObject.put("landmark", landmark);
            jsonObject.put("zip",zip);
            jsonObject.put("profile_image",profile_image);
            jsonObject.put("has_image",has_image);
            jsonObject.put("care_of", care_of);
            jsonObject.put("moblie_verified",moblie_verified);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "sendadhardata: " + jsonObject );


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_ADHAR_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("adharresp", "adharresp: " + response );

                Intent i = new Intent(getApplicationContext(),SignUpDetails.class);
                i.putExtra("adharno",enteradhar.getText().toString());
                startActivity(i);

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
                params.put("Authorization",UserAccessToken);
                Log.e(TAG, "getHeaders: " + params );
                return params;

            }
        };

        MySingleton.getInstance(GetAdharDetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void sendotpadhar() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("aadhaarNumber", enteradhar.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADHAR_OTP, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("resp", "onResponse: " + response );

                try {
                    if (response.getInt("status_code") == 200)
                    {
                        Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                        JSONObject object = response.getJSONObject("data");

                        Client_id = object.getString("client_id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String code = "null";

                if (code != null)
                {
                    code = String.valueOf(error.networkResponse.statusCode);

                    Toast.makeText(getApplicationContext(),"error code "+code,Toast.LENGTH_SHORT).show();

                    progressDialog.cancel();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"unknown server error",Toast.LENGTH_SHORT).show();
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

        MySingleton.getInstance(GetAdharDetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    public void gotoadharupload(View view) {

        Intent i = new Intent(getApplicationContext(),AdharImageUpload.class);
        i.putExtra("adharno",enteradhar.getText().toString());
        startActivity(i);

    }

    @Override
    public void onBackPressed() {

        if(progressBar.getVisibility()==View.VISIBLE)
        {
            progressBar.setVisibility(View.GONE);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Action Denied..",Toast.LENGTH_SHORT).show();
        }
      //  super.onBackPressed();

    }
}