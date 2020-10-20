package rutherfordit.com.instasalary.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.model.CityModel;

public class PersonalInfo extends AppCompatActivity {

    private static final String TAG = "FCM";
    String FCM_TOken;
    TextInputEditText enterstate, entercity, enterfullname, enterfathername, enterdob, enterdoorno, enterstreet, entercur_doorno, entercur_street, entercur_housetype, entercur_city, entercur_state, enteremail;
    Spinner statespinner, cityspinner, curr_statespinner, curr_cityspinner, curr_housetypespinner;
    RadioButton sameasadhar;
    String currstate, currcity, currhousetype, currpincode, currstreet;
    RelativeLayout personalsubmit;
    String getvaluefromsegment;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    TextInputEditText enterpincode, entercur_pincode;
    CardView loader_personal;
    boolean state_entered, city_entered = false;
    private ArrayList<CityModel> cityModelArrayList;
    private ArrayList<String> names;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        FCM_TOken = token;

                        Log.d(TAG, token);
                        // Toast.makeText(PersonalInfo.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        cityModelArrayList = new ArrayList<>();
        names = new ArrayList<String>();
        names.add("-- Select City --");

        loader_personal = findViewById(R.id.loader_personal);
        loader_personal.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        getvaluefromsegment = sharedPreferences.getString("segment_value", "");

        Log.e("getvalue", "init: " + UserAccessToken);

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

        enterfullname.setText(sharedPreferences.getString("full_name", ""));

        String dob = sharedPreferences.getString("dob", "");

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("dd-MM-yyyy").parse(dob);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String parsedDate = formatter.format(initDate);
            enterdob.setText(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String fathername = sharedPreferences.getString("care_of", "");
        fathername = fathername.replaceAll("S/O ", "");

        enterfathername.setText(fathername);

        String street = sharedPreferences.getString("street", "");
        String subdist = sharedPreferences.getString("subdist", "");
        String vtc = sharedPreferences.getString("vtc", "");
        String ioc = sharedPreferences.getString("ioc", "");
        String po = sharedPreferences.getString("po", "");

        /*if(subdist.equals(""))
        {
            enterstreet.setText("");
        }
        else
        {
            enterstreet.setText(street + " " + subdist + " " + vtc + " " + ioc + " " + po);
        }*/
        enterstreet.setText(street + " " + subdist + " " + vtc + " " + ioc + " " + po);
        enterdoorno.setText(sharedPreferences.getString("house", ""));
        enterstate.setText(sharedPreferences.getString("state", ""));
        entercity.setText(sharedPreferences.getString("vtc", ""));
        enterpincode.setText(sharedPreferences.getString("zip", ""));

        if (enterpincode.getText().toString().length() == 6) {

            String Url = "http://www.postalpincode.in/api/pincode/" + enterpincode.getText().toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONArray array = response.getJSONArray("PostOffice");

                        Log.e("pincodedata", "onResponse: " + array);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject obj = array.getJSONObject(i);

                            CityModel cityModel = new CityModel();

                            cityModel.setName(obj.getString("Name"));
                            cityModel.setDistrict(obj.getString("District"));
                            cityModel.setDivision(obj.getString("Division"));
                            cityModel.setCircle(obj.getString("Circle"));
                            cityModel.setState(obj.getString("State"));
                            cityModel.setCountry(obj.getString("Country"));

                            cityModelArrayList.add(cityModel);

                            String cty = obj.getString("Region");
                            cty = cty.replaceAll("City", "");

                            String st = obj.getString("State");

                            entercity.setText(cty);
                            enterstate.setText(st);
                            state_entered = true;
                            city_entered = true;
                            cityspinner.setEnabled(false);
                            statespinner.setEnabled(false);

                            if (enterdoorno.getText().toString().length() == 0) {
                                enterdoorno.setClickable(true);
                                enterdoorno.setFocusable(true);
                            } else {
                                enterdoorno.setClickable(false);
                                enterdoorno.setFocusable(false);
                            }

                            if (enterstreet.getText().toString().equals("")) {
                                enterstreet.setClickable(true);
                                enterstreet.setFocusable(true);
                            } else {
                                enterstreet.setClickable(false);
                                enterstreet.setFocusable(false);
                            }


                            /*enterstreet.setFocusable(false);
                            enterstreet.setClickable(false);*/
                            enterpincode.setClickable(false);
                            enterpincode.setFocusable(false);
                            enterfullname.setFocusable(false);
                            enterfullname.setClickable(false);
                            enterfathername.setFocusable(false);
                            enterfathername.setClickable(false);
                            enterdob.setFocusable(false);
                            enterdob.setClickable(false);

                            /*names.add(obj.getString("Name"));

                            ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, names);

                            cityspinner.setAdapter(aa);*/

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

        } else {
            state_entered = false;
            city_entered = false;
            cityspinner.setEnabled(true);
            statespinner.setEnabled(true);
            enterdoorno.setClickable(true);
            enterdoorno.setFocusable(true);
            enterstreet.setFocusable(true);
            enterstreet.setClickable(true);
            enterpincode.setClickable(true);
            enterpincode.setFocusable(true);
            enterfullname.setFocusable(true);
            enterfullname.setClickable(true);
            enterfathername.setFocusable(true);
            enterfathername.setClickable(true);
            enterdob.setFocusable(true);
            enterdob.setClickable(true);
        }

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

                if (!state_entered) {
                    statespinner.performClick();
                } else {
                    Toasty.warning(getApplicationContext(), "Denied..", Toast.LENGTH_SHORT).show();
                }

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

                        /*String cityname = parent.getItemAtPosition(pos).toString();

                        if (!cityname.equals("-- Select City --"))
                        {

                            *//*if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                                    enteravgincome.getText().toString().length() > 4 && !Vendor.equals("-- Select Vendor --") && gotdocuments)
                            {
                                ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                                click = true;
                            } else {
                                ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                                click = false;
                            }*//*

                          //  String state = cityModelArrayList.get(pos-1).getState();

                        //    Log.e("state", "onItemSelected: " + state );

                          //  enterstate.setText(state);
                        }

                        *//*String state = cityModelArrayList.get(pos-1).getState();

                        Log.e("state", "onItemSelected: " + state );*//**/

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        entercity.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (!city_entered) {
                    cityspinner.performClick();
                } else {
                    Toasty.warning(getApplicationContext(), "Denied..", Toast.LENGTH_SHORT).show();
                }
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
                    entercur_state.setText(enterstate.getText().toString());
                    entercur_city.setText(entercity.getText().toString());

                } else {
                    sameasadhar.setChecked(false);
                    sameasadhar.setSelected(false);
                    entercur_doorno.setText("");
                    entercur_street.setText("");
                    entercur_pincode.setText("");
                    entercur_state.setSelection(0);
                    entercur_city.setText("-- Select City --");
                    entercur_state.setText("-- Select State --");
                    entercur_city.setSelection(0);
                }
            }
        });

        enterfullname.addTextChangedListener(new TextWatcher() {

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

                /*if (enterpincode.getText().toString().length()==6)
                {
                    String Url = "http://www.postalpincode.in/api/pincode/"+enterpincode.getText().toString();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray array = response.getJSONArray("PostOffice");

                                Log.e("pincodedata", "onResponse: " + array );

                                for (int i = 0 ; i < array.length() ; i++)
                                {

                                    JSONObject obj = array.getJSONObject(i);

                                    CityModel cityModel = new CityModel();

                                    cityModel.setName(obj.getString("Name"));
                                    cityModel.setDistrict(obj.getString("District"));
                                    cityModel.setDivision(obj.getString("Division"));
                                    cityModel.setCircle(obj.getString("Circle"));
                                    cityModel.setState(obj.getString("State"));
                                    cityModel.setCountry(obj.getString("Country"));

                                    cityModelArrayList.clear();

                                    cityModelArrayList.add(cityModel);

                                    names.add(obj.getString("Name"));

                                    ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, names);

                                    cityspinner.setAdapter(aa);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                }
                else */
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

                                if (month < 10) {

                                    formattedMonth = "0" + month;
                                }
                                if (dayOfMonth < 10) {

                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                enterdob.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.instapink));
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.instapink));
                } else {
                    picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                }

            }
        });


        personalsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (personalsubmit.isEnabled()) {
                    loader_personal.setVisibility(View.VISIBLE);
                  //  cibilRequest();
                    request();
                }
            }
        });
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    private void cibilRequest() {

        String Statecode = "";
        String enteredStateCode = enterstate.getText().toString();
        String newenteredStateCode = capitalize(enteredStateCode);
        //textView.setText(chars);

        Log.e(TAG, "cibilRequest: " + enteredStateCode + newenteredStateCode);

        if (newenteredStateCode.equals("Andhra Pradesh")) {
            Statecode = "AP";
        } else if (newenteredStateCode.equals("Arunachal Pradesh")) {
            Statecode = "AR";
        } else if (newenteredStateCode.equals("Assam")) {
            Statecode = "AS";
        } else if (newenteredStateCode.equals("Bihar")) {
            Statecode = "BR";
        } else if (newenteredStateCode.equals("Chattisgarh")) {
            Statecode = "CG";
        } else if (newenteredStateCode.equals("Chandigarh")) {
            Statecode = "CG";
        } else if (newenteredStateCode.equals("Daman & Diu")) {
            Statecode = "DD";
        } else if (newenteredStateCode.equals("Delhi")) {
            Statecode = "DL";
        } else if (newenteredStateCode.equals("Dadra & Nagar Haveli")) {
            Statecode = "DN";
        } else if (newenteredStateCode.equals("Goa")) {
            Statecode = "GA";
        } else if (newenteredStateCode.equals("Gujarat")) {
            Statecode = "GJ";
        } else if (newenteredStateCode.equals("Himachal Pradesh")) {
            Statecode = "HP";
        } else if (newenteredStateCode.equals("Haryana")) {
            Statecode = "HR";
        } else if (newenteredStateCode.equals("Jharkhand")) {
            Statecode = "JH";
        } else if (newenteredStateCode.equals("Jammu & Kashmir")) {
            Statecode = "JK";
        } else if (newenteredStateCode.equals("Karnataka")) {
            Statecode = "KA";
        } else if (newenteredStateCode.equals("Kerala")) {
            Statecode = "KL";
        } else if (newenteredStateCode.equals("Lakshadweep")) {
            Statecode = "LD";
        } else if (newenteredStateCode.equals("Maharashtra")) {
            Statecode = "MH";
        } else if (newenteredStateCode.equals("Meghalaya")) {
            Statecode = "ML";
        } else if (newenteredStateCode.equals("Manipur")) {
            Statecode = "MN";
        } else if (newenteredStateCode.equals("Madhya Pradesh")) {
            Statecode = "MP";
        } else if (newenteredStateCode.equals("Mizoram")) {
            Statecode = "MZ";
        } else if (newenteredStateCode.equals("Nagaland")) {
            Statecode = "NL";
        } else if (newenteredStateCode.equals("Orissa")) {
            Statecode = "OR";
        } else if (newenteredStateCode.equals("Punjab")) {
            Statecode = "PB";
        } else if (newenteredStateCode.equals("Pondicherry")) {
            Statecode = "PY";
        } else if (newenteredStateCode.equals("Puducherry")) {
            Statecode = "PY";
        } else if (newenteredStateCode.equals("Rajasthan")) {
            Statecode = "RJ";
        } else if (newenteredStateCode.equals("Sikkim")) {
            Statecode = "SK";
        } else if (newenteredStateCode.equals("Telangana")) {
            Statecode = "TG";
        } else if (newenteredStateCode.equals("Tamil Nadu")) {
            Statecode = "TN";
        } else if (newenteredStateCode.equals("Tripura")) {
            Statecode = "TR";
        } else if (newenteredStateCode.equals("Uttaranchal")) {
            Statecode = "UL";
        } else if (newenteredStateCode.equals("Uttarakhand")) {
            Statecode = "UL";
        } else if (newenteredStateCode.equals("Uttar Pradesh")) {
            Statecode = "UP";
        } else if (newenteredStateCode.equals("West Bengal")) {
            Statecode = "WB";
        }

        String address = enterdoorno.getText().toString() + " " + enterstreet.getText().toString() + " " + entercity.getText().toString()
                + " " + enterstate.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullname", enterfullname.getText().toString());
            jsonObject.put("dob", enterdob.getText().toString());
            jsonObject.put("address", address);
            jsonObject.put("stateCode", Statecode);
            jsonObject.put("pincode", enterpincode.getText().toString());
            jsonObject.put("mobile", sharedPreferences.getString("Phone", ""));
            jsonObject.put("aadhaarNumber", sharedPreferences.getString("aadhar_number", ""));
            jsonObject.put("panNumber", sharedPreferences.getString("Pan", ""));
            jsonObject.put("motherName", "");
            jsonObject.put("fatherName", enterfathername.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("address", "cibil: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CIBIL_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toasty.success(getApplicationContext(), "CIBIL REPORT GENERATED..", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error Response", "onErrorResponse: " + error.getLocalizedMessage());

                /*int code = error.networkResponse.statusCode;

                if (code == 422)
                {

                    Toast.makeText(getApplicationContext(),"Please fill the fields properly..",Toast.LENGTH_SHORT).show();
                }*/

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

        MySingleton.getInstance(PersonalInfo.this).addToRequestQueue(jsonObjectRequest);

    }

    private void request() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", enterfullname.getText().toString());
            jsonObject.put("father_name", enterfathername.getText().toString());
            jsonObject.put("dob", enterdob.getText().toString());
            jsonObject.put("email", enteremail.getText().toString());
            jsonObject.put("door_no", entercur_doorno.getText().toString());
            jsonObject.put("street", entercur_street.getText().toString());
            jsonObject.put("city", entercity.getText().toString());
            jsonObject.put("state", enterstate.getText().toString());
            jsonObject.put("house_type", currhousetype);
            jsonObject.put("house_no", enterdoorno.getText().toString());
            jsonObject.put("address_type", "2");
            jsonObject.put("pincode", enterpincode.getText().toString());
            jsonObject.put("androidfcm_id", FCM_TOken);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("address", "request: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SAVE_ADDRESS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                if (response != null) {
                    loader_personal.setVisibility(View.GONE);

                    if (getvaluefromsegment.equals("salaried")) {
                        Intent i = new Intent(getApplicationContext(), ProfessionalInfo.class);
                        startActivity(i);
                    } else if (getvaluefromsegment.equals("owner")) {
                        Intent i = new Intent(getApplicationContext(), DriverProfessionalInfo.class);
                        startActivity(i);
                    }
                } else {
                    loader_personal.setVisibility(View.GONE);
                    Toasty.info(getApplicationContext(), "Response is null..", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                int code = error.networkResponse.statusCode;

                if (code == 422) {

                    Toasty.warning(getApplicationContext(), "Please fill the fields properly..", Toast.LENGTH_SHORT).show();
                }

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

        MySingleton.getInstance(PersonalInfo.this).addToRequestQueue(jsonObjectRequest);
    }
}
