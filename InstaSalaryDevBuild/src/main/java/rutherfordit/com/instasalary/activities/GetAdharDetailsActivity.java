package rutherfordit.com.instasalary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

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

public class GetAdharDetailsActivity extends AppCompatActivity {

    private static final String TAG = "adhar";
    TextInputEditText enteradhar;
    RelativeLayout Adharsubmit;
    SharedPreferences sharedPreferences;
    String UserAccessToken, Client_id;
    BottomSheetDialog bottomSheetDialog;
    View view;
    boolean click = false;
    Button submitadharotp;
    EditText enteradharotp;
    LinearLayout otp_layout;
    String mypreference = "mySharedPreference";
    String emailhash, mobilehash, client_id, full_name, aadhar_number, dob, gender, country, dist, state, po, ioc, vtc, subdist, street, house, landmark, zip, has_image, care_of, moblie_verified;
    String profile_image = "";
    JSONObject data, address;
    CardView loader_adharnumber, loader_adharnumberOTP;
    String AdharOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_adhar_details);

        init();
    }

    private void init() {

      //  OtpReader.bind(this,"AADHAAR");
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        enteradhar = findViewById(R.id.enteradharNumber);
        Adharsubmit = findViewById(R.id.AdharsubmitButton);
        loader_adharnumber = findViewById(R.id.loader_adharnumber);
        loader_adharnumber.setVisibility(View.GONE);

        view = getLayoutInflater().inflate(R.layout.adhar_bottom_sheet, null);
        otp_layout = view.findViewById(R.id.otp_layout);
        enteradharotp = view.findViewById(R.id.enteradharotp);
        submitadharotp = view.findViewById(R.id.submitadharotp);
        loader_adharnumberOTP = view.findViewById(R.id.loader_adharnumberOTP);
        bottomSheetDialog = new BottomSheetDialog(GetAdharDetailsActivity.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);

        enteradhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(enteradhar.getText().toString()) && enteradhar.getText().toString().trim().length() == 12) {
                    loader_adharnumber.setVisibility(View.VISIBLE);
                    validateAdhar();
                    /*bottomSheetDialog.show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("aadhar_number", enteradhar.getText().toString());
                    editor.apply();
                    sendotpadhar();*/
                } else {
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

                if (enteradharotp.getText().toString().length() > 5) {
                    loader_adharnumberOTP.setVisibility(View.VISIBLE);
                    // otp_layout.setVisibility(View.GONE);
                    getadhardetails();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter 6 Digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void validateAdhar() {

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

                    if (response.getString("success").equals("true")) {
                        sendotpadhar();
                        Toast.makeText(getApplicationContext(), "Validation Successfull", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("aadhar_number", enteradhar.getText().toString());
                        editor.apply();
                    } else {
                        Toast.makeText(getApplicationContext(), "Adhar Number Doesnot exist... Please Enter Correct Adhar Number", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_adharnumber.setVisibility(View.GONE);
                bottomSheetDialog.cancel();

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toast.makeText(getApplicationContext(), "Please Enter Correct Adhar Number..", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                } else if (code == 504) {
                    Toast.makeText(getApplicationContext(), "Server Timeout..", Toast.LENGTH_SHORT).show();
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

    private void getadhardetails() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("generateOTPReferenceId", Client_id);
            jsonObject.put("otp", enteradharotp.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "getadhardetails: " + jsonObject + Client_id);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADHAR_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "onResponse: " + response);

                try {

                    if (response.getString("success").equals("true")) {

                        data = response.getJSONObject("payload");
                        address = data.getJSONObject("address");

                        //  Log.e(TAG, "onResponse: " + address );

                        client_id = data.getString("uidaiReferenceId");
                        full_name = data.getString("fullName");
                        String care_of = data.getString("careOf");
                        care_of = care_of.replaceAll("S/O ", "");
                        care_of = care_of.replaceAll("S/O:", "");
                        aadhar_number = data.getString("aadhaarNumber");
                        dob = data.getString("dob");
                        gender = data.getString("gender");
                        emailhash = data.getString("emailHash");
                        mobilehash = data.getString("mobileHash");

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
                        zip = data.getString("postalCode");


                        profile_image = data.getString("profileImage");

                        String a = data.getString("profileImage");

                        byte[] decodedString = Base64.decode(a, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        Log.e(TAG, "onResponse: " + decodedByte);

                        has_image = data.getString("hasImage");

                        moblie_verified = " ";

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("client_id", client_id);
                        editor.putString("full_name", full_name);
                        //   editor.putString("aadhar_number", aadhar_number);
                        editor.putString("dob", dob);
                        editor.putString("gender", gender);
                        editor.putString("country", country);
                        editor.putString("emailhash", emailhash);
                        editor.putString("mobilehash", mobilehash);
                        editor.putString("dist", dist);
                        editor.putString("state", state);
                        editor.putString("po", po);
                        editor.putString("ioc", ioc);
                        editor.putString("vtc", vtc);
                        editor.putString("subdist", subdist);
                        editor.putString("street", street);
                        editor.putString("house", house);
                        editor.putString("landmark", landmark);
                        editor.putString("zip", zip);
                        editor.putString("profile_image", profile_image);
                        editor.putString("has_image", has_image);
                        editor.putString("care_of", care_of);
                        editor.putString("moblie_verified", moblie_verified);

                        editor.apply();


                        Adharsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        Adharsubmit.setEnabled(true);
                        click = true;
                        Toast.makeText(getApplicationContext(), "DATA RETREIVED", Toast.LENGTH_SHORT).show();
                        sendadhardata();

                    } else {
                        loader_adharnumberOTP.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_adharnumberOTP.setVisibility(View.GONE);

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toast.makeText(getApplicationContext(), "Enter Correct Adhar OTP..", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toast.makeText(getApplicationContext(), "Api Error..", Toast.LENGTH_SHORT).show();
                } else if (code == 461) {
                    Toast.makeText(getApplicationContext(), "Enter Correct OTP..", Toast.LENGTH_SHORT).show();
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

    private void sendadhardata() {

        loader_adharnumberOTP.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("client_id", client_id);
            jsonObject.put("full_name", full_name);
            jsonObject.put("aadhar_number", aadhar_number);
            jsonObject.put("dob", dob);
            jsonObject.put("gender", gender);
            jsonObject.put("country", country);
            jsonObject.put("dist", dist);
            jsonObject.put("state", state);
            jsonObject.put("po", po);
            jsonObject.put("ioc", ioc);
            jsonObject.put("vtc", vtc);
            jsonObject.put("subdist", subdist);
            jsonObject.put("street", street);
            jsonObject.put("house", house);
            jsonObject.put("landmark", landmark);
            jsonObject.put("zip", zip);
            jsonObject.put("profile_image", profile_image);
            jsonObject.put("has_image", has_image);
            jsonObject.put("care_of", care_of);
            jsonObject.put("moblie_verified", moblie_verified);

            Log.e(TAG, "sendadhardata: " + jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "sendadhardata: " + jsonObject);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_ADHAR_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("adharresp", "adharresp: " + response);

                loader_adharnumberOTP.setVisibility(View.GONE);

                Intent i = new Intent(getApplicationContext(), SignUpDetails.class);
                i.putExtra("adharno", enteradhar.getText().toString());
                startActivity(i);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toast.makeText(getApplicationContext(), "Adhar Sending 422 error..", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toast.makeText(getApplicationContext(), "Adhar Sending 500 error..", Toast.LENGTH_SHORT).show();
                }

                loader_adharnumberOTP.setVisibility(View.GONE);
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

        MySingleton.getInstance(GetAdharDetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void sendotpadhar() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("aadhaarNumber", enteradhar.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "sendotpadhar: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADHAR_OTP, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("resp", "onResponse: " + response);

                loader_adharnumberOTP.setVisibility(View.GONE);

                try {
                    if (response.getString("success").equals("true")) {

                        JSONObject obj = response.getJSONObject("payload");

                        Client_id = obj.getString("generateOTPReferenceId");

                        Toast.makeText(getApplicationContext(), "OTP Sent..", Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "onErrorResponse: " + error.networkResponse.statusCode);

                loader_adharnumberOTP.setVisibility(View.GONE);

               /* int code = error.networkResponse.statusCode;

                if (code == 422)
                {

                    Toast.makeText(getApplicationContext(),"error code "+code,Toast.LENGTH_SHORT).show();
                }
                else if (code == 500)
                {
                    Toast.makeText(getApplicationContext(),"Internal server error",Toast.LENGTH_SHORT).show();
                }
                else if (code == 461)
                {
                    Toast.makeText(getApplicationContext(),"UIDAI Servers are facing some technical issue. Please try again after some time",Toast.LENGTH_SHORT).show();
                }*/

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

        /*jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        /*jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

    }

    public void gotoadharupload(View view) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("client_id", "");
        editor.putString("full_name", "");
        editor.putString("dob", "");
        editor.putString("gender", "");
        editor.putString("country", "");
        editor.putString("emailhash", "");
        editor.putString("mobilehash", "");
        editor.putString("dist", "");
        editor.putString("state", "");
        editor.putString("po", "");
        editor.putString("ioc", "");
        editor.putString("vtc", "");
        editor.putString("subdist", "");
        editor.putString("street", "");
        editor.putString("house", "");
        editor.putString("landmark", "");
        editor.putString("zip", "");
        editor.putString("profile_image", "");
        editor.putString("has_image", "");
        editor.putString("care_of", "");
        editor.putString("moblie_verified", "");

        editor.apply();

        Intent i = new Intent(getApplicationContext(), AdharImageUpload.class);
        i.putExtra("adharno", enteradhar.getText().toString());
        startActivity(i);

    }

    @Override
    public void onBackPressed() {

        if (loader_adharnumber.getVisibility() == View.VISIBLE) {
            loader_adharnumber.setVisibility(View.GONE);
        } else {
            Toast.makeText(getApplicationContext(), "Action Denied..", Toast.LENGTH_SHORT).show();
        }
        //  super.onBackPressed();



    }
}