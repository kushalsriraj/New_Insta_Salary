package com.huygenslabs.instasalarystaging.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfo extends AppCompatActivity {

    ProgressDialog progressBar;
    TextInputEditText enterstate, entercity, enterfullname, enterfathername, enterdob, enterdoorno, enterstreet, entercur_doorno, entercur_street, entercur_housetype, entercur_city, entercur_state,enteremail;
    Spinner statespinner, cityspinner, curr_statespinner, curr_cityspinner, curr_housetypespinner;
    RadioButton sameasadhar;
    String currstate, currcity,currhousetype,currpincode,currstreet;
    RelativeLayout personalsubmit;
    String getvaluefromsegment;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    TextInputEditText enterpincode,entercur_pincode;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        progressBar = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        getvaluefromsegment = getIntent().getStringExtra("sendfromsignup");

        Log.e("getvalue", "init: " + getvaluefromsegment );

        personalsubmit = findViewById(R.id.personalsubmit);

        sameasadhar = findViewById(R.id.sameasadhar);
        statespinner = findViewById(R.id.statespinner);
        enterstate = findViewById(R.id.enterstate);
        entercity = findViewById(R.id.entercity);
        cityspinner = findViewById(R.id.cityspinner);
        enterfullname = findViewById(R.id.enterfullname);
        enteremail = findViewById(R.id.enteremail);

        entercur_housetype = findViewById(R.id.entercur_housetype);
        entercur_city = findViewById(R.id.entercur_city);
        entercur_state = findViewById(R.id.entercur_state);
        curr_statespinner = findViewById(R.id.curr_statespinner);
        curr_cityspinner = findViewById(R.id.curr_cityspinner);
        curr_housetypespinner = findViewById(R.id.curr_housetypespinner);

        entercur_street = findViewById(R.id.entercur_street);
        enterfathername = findViewById(R.id.enterfathername);
        enterdob = findViewById(R.id.enterdob);
        enterdoorno = findViewById(R.id.enterdoorno);
        enterstreet = findViewById(R.id.enterstreet);
        entercur_doorno = findViewById(R.id.entercur_doorno);
        enterpincode = findViewById(R.id.enterpincode);
        entercur_pincode = findViewById(R.id.entercur_pincode);

        enterfullname.setText(sharedPreferences.getString("full_name",""));
        enterdob.setText(sharedPreferences.getString("dob",""));
        enterfathername.setText(sharedPreferences.getString("care_of",""));

        String street = sharedPreferences.getString("street","");
        String subdist = sharedPreferences.getString("subdist","");
        String vtc = sharedPreferences.getString("vtc","");
        String ioc = sharedPreferences.getString("ioc","");
        String po = sharedPreferences.getString("po","");

        enterdoorno.setText(sharedPreferences.getString("house",""));
        enterstreet.setText(street + " " + subdist + " " + vtc + " " + ioc + " " + po);
        enterstate.setText(sharedPreferences.getString("state",""));
        entercity.setText(sharedPreferences.getString("vtc",""));
        enterpincode.setText(sharedPreferences.getString("zip",""));

        curr_statespinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        entercur_state.setText(parent.getItemAtPosition(pos).toString());

                        entercur_state.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        entercur_state.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                curr_statespinner.performClick();
            }
        });

        statespinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        currstate = parent.getItemAtPosition(pos).toString();

                        enterstate.setText(parent.getItemAtPosition(pos).toString());

                        enterstate.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        enterstate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                statespinner.performClick();
            }
        });

        curr_cityspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        entercur_city.setText(parent.getItemAtPosition(pos).toString());

                        entercur_city.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        entercur_city.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                curr_cityspinner.performClick();
            }
        });

        cityspinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        currcity = parent.getItemAtPosition(pos).toString();

                        entercity.setText(parent.getItemAtPosition(pos).toString());

                        entercity.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        entercity.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                cityspinner.performClick();
            }
        });

        entercur_housetype.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                curr_housetypespinner.performClick();
            }
        });

        curr_housetypespinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        currhousetype = parent.getItemAtPosition(pos).toString();

                        entercur_housetype.setText(parent.getItemAtPosition(pos).toString());

                        entercur_housetype.setCursorVisible(false);

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        sameasadhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sameasadhar.isSelected()) {
                    sameasadhar.setChecked(true);
                    sameasadhar.setSelected(true);
                    entercur_doorno.setText(enterdoorno.getText().toString());
                    entercur_street.setText(enterstreet.getText().toString());
                    entercur_pincode.setText(enterpincode.getText().toString());
                    entercur_state.setText(currstate);
                    entercur_city.setText(currcity);
                } else {
                    sameasadhar.setChecked(false);
                    sameasadhar.setSelected(false);
                    entercur_doorno.setText("");
                    entercur_street.setText("");
                    entercur_pincode.setText("");
                    entercur_state.setSelection(0);
                    entercur_city.setSelection(0);
                }
            }
        });

        enterfullname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0 ) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterfathername.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterdob.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterdoorno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterstreet.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercur_doorno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercur_street.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enteremail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterpincode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercur_pincode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterfullname.getText().toString().length() > 0 && enterfathername.getText().toString().length() > 0 && enterdob.getText().toString().length() > 0
                        && enterdoorno.getText().toString().length() > 0 && enterstreet.getText().toString().length() > 0 && entercur_doorno.getText().toString().length() > 0
                        && entercur_street.getText().toString().length() > 0 && enteremail.getText().toString().length() > 0
                        && enterpincode.getText().toString().length() > 0 && entercur_pincode.getText().toString().length() > 0) {
                    personalsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    personalsubmit.setEnabled(true);
                } else {
                    personalsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    personalsubmit.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterdob.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);


                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(PersonalInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                int month = monthOfYear + 1;
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;

                                if(month < 10){

                                    formattedMonth = "0" + month;
                                }
                                if(dayOfMonth < 10){

                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                enterdob.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.instapink));
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.instapink));
                }
                else
                {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }

            }
        });


        personalsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (personalsubmit.isEnabled())
                {
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    request();
                }
            }
        });
    }

    private void request()
    {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",enterfullname.getText().toString());
            jsonObject.put("father_name",enterfathername.getText().toString());
            jsonObject.put("dob",enterdob.getText().toString());
            jsonObject.put("email",enteremail.getText().toString());
            jsonObject.put("door_no",entercur_doorno.getText().toString());
            jsonObject.put("street",entercur_street.getText().toString());
            jsonObject.put("city",currcity);
            jsonObject.put("state",currstate);
            jsonObject.put("house_type",currhousetype);
            jsonObject.put("house_no",enterdoorno.getText().toString());
            jsonObject.put("address_type","2");
            jsonObject.put("pincode",enterpincode.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SAVE_ADDRESS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                if (response!=null)
                {
                    Intent i = new Intent(getApplicationContext(),ProfessionalInfo.class);
                    i.putExtra("segment",getvaluefromsegment);
                    startActivity(i);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int code = error.networkResponse.statusCode;

                if (code == 422)
                {
                    Toast.makeText(getApplicationContext(),"Please fill the fields properly..",Toast.LENGTH_SHORT).show();
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization",UserAccessToken);
                return params;
            }
        };

        MySingleton.getInstance(PersonalInfo.this).addToRequestQueue(jsonObjectRequest);
    }
}
