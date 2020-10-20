package rutherfordit.com.instasalary.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;

public class SignUpDetails extends AppCompatActivity {

    RelativeLayout pansubmit;
    TextInputEditText enterpan;
    ImageView backarrowsignupdetails;
    SharedPreferences sharedPreferences;
    String Panno, UserAccessToken;
    boolean click = false;
    String Adharno;
    Button upload_pan;
    ImageView uploadsuccess_pan;
    TextView upload_pan_text;
    String message;
    boolean gotpan = false;
    CardView loader_pannumber;
    private int GoToPanUpload = 1;

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        Toasty.warning(getApplicationContext(), "Action Denied..", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);

        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoToPanUpload) {
            assert data != null;
            message = data.getStringExtra("MESSAGE");

            assert message != null;
            if (message.equals("Success")) {
                gotpan = true;
                uploadsuccess_pan.setVisibility(View.VISIBLE);
                upload_pan_text.setText("Upload Successfull ");
                upload_pan.setText("Reupload");

                Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

                // Matcher matcher = pattern.matcher(sequence);

                if (!TextUtils.isEmpty(enterpan.getText().toString()) && enterpan.getText().toString().trim().length() == 10 && gotpan) {
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

            } else {
                gotpan = false;
            }

            Log.e("result", "onActivityResult:  " + message);
        }

    }

    private void init() {

        //  Adharno = getIntent().getStringExtra("adharno");
        pansubmit = findViewById(R.id.pansubmit);
        enterpan = findViewById(R.id.enterpan);
        upload_pan_text = findViewById(R.id.upload_pan_text);
        uploadsuccess_pan = findViewById(R.id.uploadsuccess_pan);
        uploadsuccess_pan.setVisibility(View.GONE);
        upload_pan = findViewById(R.id.upload_pan);
        loader_pannumber = findViewById(R.id.loader_pannumber);
        loader_pannumber.setVisibility(View.GONE);

        enterpan.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), new InputFilter.AllCaps()});

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);

        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        Adharno = sharedPreferences.getString("aadhar_number", "");

        backarrowsignupdetails = findViewById(R.id.backarrowsignupdetails);

        onclicks();

    }

    private void onclicks() {

        upload_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPanupload();
            }
        });

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

                if (!TextUtils.isEmpty(enterpan.getText().toString()) && enterpan.getText().toString().trim().length() == 10 && matcher.matches() && gotpan) {
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

                if (click && gotpan) {

                    Panno = enterpan.getText().toString();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Pan", Panno);
                    editor.apply();

                    loader_pannumber.setVisibility(View.VISIBLE);

                    validatepan();


                } else if (click) {
                    Toasty.info(getApplicationContext(), "Please Upload PAN image", Toast.LENGTH_LONG).show();
                } else if (gotpan) {
                    Toasty.info(getApplicationContext(), "Please Enter PAN Number", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void validatepan() {

        final JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("pan", Panno);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "EnterPersonalDetails: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.PAN_VALIDATION, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    sendpandata();
                    EnterPersonalDetails();
                    Toasty.warning(getApplicationContext(), response.getString("success"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader_pannumber.setVisibility(View.GONE);
                int code = error.networkResponse.statusCode;
                if (code == 422) {
                    Toasty.error(getApplicationContext(), "Enter correct pan number", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toasty.error(getApplicationContext(), "Api Error..", Toast.LENGTH_SHORT).show();
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

    private void sendpandata() {

        final JSONObject jsonObject = new JSONObject();

        try {

            String statecode = "";

            String fathername = sharedPreferences.getString("care_of", "");
            fathername = fathername.replaceAll("S/O ", "");

            String mobile = sharedPreferences.getString("Phone", "");
            String pincode = sharedPreferences.getString("zip", "");
            // String statecode = sharedPreferences.getString("care_of","");
            String dob = sharedPreferences.getString("dob", "");

            String street = sharedPreferences.getString("street", "");
            String subdist = sharedPreferences.getString("subdist", "");
            String vtc = sharedPreferences.getString("vtc", "");
            String ioc = sharedPreferences.getString("ioc", "");
            String po = sharedPreferences.getString("po", "");
            String house = sharedPreferences.getString("house", "");
            String state = sharedPreferences.getString("state", "");
            String dist = sharedPreferences.getString("dist", "");

            String address = house + street + vtc + ioc + po + subdist + dist + state;

            jsonObject.put("name", sharedPreferences.getString("full_name", ""));
            jsonObject.put("pan_number", enterpan.getText().toString());

            /*jsonObject.put("inquiryPurpose","PERSONAL_LOAN" );
            jsonObject.put("dob", dob);
            jsonObject.put("address", address);
            jsonObject.put("stateCode", state);
            jsonObject.put("pincode",pincode);
            jsonObject.put("mobile", mobile );
            jsonObject.put("aadhaarNumber", Adharno);
            jsonObject.put("fatherName", fathername);*/


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("tag", "EnterPersonalDetails: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_PAN_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("pandata", "onResponse: " + response);

                try {
                    JSONObject dataobj = response.getJSONObject("data");

                    Log.e("data", "obj: " + dataobj);

                    if (dataobj.length() == 0) {
                        Toasty.warning(getApplicationContext(), "Data Is Not Saved", Toast.LENGTH_SHORT).show();
                        loader_pannumber.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader_pannumber.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                Log.e("pandata", "getHeaders: " + params);
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

        Log.e("tag", "EnterPersonalDetails: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.PERSONAL_DETAILS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject dataobj = response.getJSONObject("data");

                    Log.e("data", "obj: " + dataobj);

                    if (dataobj.length() == 0) {
                        Toasty.warning(getApplicationContext(), "Data Is Not Saved", Toast.LENGTH_SHORT).show();
                        loader_pannumber.setVisibility(View.GONE);
                    } else {
                        Toasty.success(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SegmentActivity.class);
                        startActivity(intent);
                        loader_pannumber.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toasty.error(getApplicationContext(), "Error code 422.", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toasty.error(getApplicationContext(), "Internal Server Error..", Toast.LENGTH_SHORT).show();
                }

                loader_pannumber.setVisibility(View.GONE);
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

    public void gotoPanupload() {

        Intent intent = new Intent(getApplicationContext(), PanImageUpload.class);
        startActivityForResult(intent, GoToPanUpload);

    }

}
