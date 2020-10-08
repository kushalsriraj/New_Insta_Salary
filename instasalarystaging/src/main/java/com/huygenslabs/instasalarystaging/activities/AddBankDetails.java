package com.huygenslabs.instasalarystaging.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddBankDetails extends AppCompatActivity {

    TextInputEditText enterbankname,enterbankbranch,enteraccno,enterbankifsc,enterloanamount,enterloanrepay;
    RelativeLayout bankdetailssubmit;
    ProgressDialog progressBar;
    String UserAccessToken;
    SharedPreferences sharedPreferences;
    boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_details);

        init();
    }

    private void init()
    {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        progressBar = new ProgressDialog(this);
        enterbankname = findViewById(R.id.enterbankname);
        enterbankbranch = findViewById(R.id.enterbankbranch);
        enteraccno = findViewById(R.id.enteraccno);
        enterbankifsc = findViewById(R.id.enterbankifsc);
        enterbankifsc.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        bankdetailssubmit =findViewById(R.id.bankdetailssubmit);

        enterloanamount = findViewById(R.id.enterloanamount);
        enterloanrepay =findViewById(R.id.enterloanrepay);

        enterbankname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterbankbranch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enteraccno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterbankifsc.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterloanamount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterloanrepay.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String added_number = enterloanrepay.getText().toString();
                if (added_number.length() != 0) {
                    int number  = Integer.parseInt(added_number);

                    if (number > 30)
                    {
                        Toast.makeText(getApplicationContext(), "Not more than 30", Toast.LENGTH_SHORT).show();
                    }
                }

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() > 4
                        && enterloanamount.getText().toString().length() > 0 && enterloanrepay.getText().toString().length() > 0) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        bankdetailssubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click)
                {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                  //  loandetailsrequest();
                    bankdetailsrequest();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fill All Details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loandetailsrequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount",enterloanamount.getText().toString());
            jsonObject.put("days",enterloanrepay.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CREATE_LOAN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("owner_response", "onResponse: " + response );

                if(response!=null)
                {
                    Toast.makeText(getApplicationContext(),"Loan Details Saved",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Not Saved",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Fill loans properly",Toast.LENGTH_SHORT).show();
                progressBar.cancel();
                  Log.e("eeeeeee", "onErrorResponse1: " + error.toString() );

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
                //  Log.e(":parama", "getHeaders: " + params );
                return params;
            }
        };

        MySingleton.getInstance(AddBankDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    private void bankdetailsrequest()
    {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bank_name",enterbankname.getText().toString());
            jsonObject.put("bank_branch",enterbankbranch.getText().toString());
            jsonObject.put("ac_number",enteraccno.getText().toString());
            jsonObject.put("bank_ifcs",enterbankifsc.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADD_BANK_DETAILS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("owner_response", "onResponse: " + response );

                if (response.has("error"))
                {
                    try {

                        JSONObject errobj = response.getJSONObject("errors");

                        if (errobj.has("ac_number"))
                        {
                            JSONArray errorarr = response.getJSONObject("errors").getJSONArray("ac_number");

                            for (int i = 0; i < errorarr.length(); i++) {
                                String value = errorarr.getString(0);
                                Toast.makeText(getApplicationContext(),""+value,Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (errobj.has("bank_ifcs"))
                        {
                            JSONArray errorarr = response.getJSONObject("errors").getJSONArray("ac_number");

                            for (int i = 0; i < errorarr.length(); i++) {
                                String value = errorarr.getString(0);
                                Toast.makeText(getApplicationContext(),""+value,Toast.LENGTH_SHORT).show();
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (response.has("data"))
                {
                    Toast.makeText(getApplicationContext(),"Saved Bank Details Succesfully",Toast.LENGTH_SHORT).show();
                    progressBar.cancel();
                    Intent i = new Intent(getApplicationContext(),CreditScoreSctivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Fill the fields properly",Toast.LENGTH_SHORT).show();
                progressBar.cancel();
                Log.e("eeeeeee", "onErrorResponse2: " + error.toString() );

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
              //  Log.e(":parama", "getHeaders: " + params );
                return params;
            }
        };

        MySingleton.getInstance(AddBankDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    public void goback(View view) {
        onBackPressed();
    }
}
