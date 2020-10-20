package rutherfordit.com.instasalary.activities;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;


public class ProfessionalInfo extends AppCompatActivity {

    LinearLayout company_Data_Layout;
    private static final String TAG = "Personalinfo";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    String apiKey = "AIzaSyB1DUTvnjbrzsr8EhXHb-Vu9KKWvqX05pU";
    String message;
    TextInputEditText Select_Place,entercompanyname, enterjobrole, enterexp, entercompanyemail, entercompanydoorno, entercompanystreet, entercompanycity, entercompanystate, entercompanypincode;
    RelativeLayout salariedprofsubmit;
    boolean click = false;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    ProgressDialog progressBar;
    Button bankstatement_professional;
    boolean status = false;
    boolean gotdocuments = false;
    TextView text_upload_professional;
    ImageView uploadsuccess_professional;
    private int GoToDocUpload = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_info);

        initsalaried();

    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoToDocUpload) {
            assert data != null;
            message = data.getStringExtra("MESSAGE");

            assert message != null;
            if (message.equals("Success")) {
                gotdocuments = true;
                status = true;

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 4) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    uploadsuccess_professional.setVisibility(View.VISIBLE);
                    text_upload_professional.setText("Documents Uploaded");
                    bankstatement_professional.setVisibility(View.GONE);
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }

            }
        }

        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                Place place = null;
                if (data != null)
                {
                    place = Autocomplete.getPlaceFromIntent(data);
                }
               // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + place.getLatLng() + place.getAddress());
                Select_Place.setText("Name : " + place.getName() + "\n" + "LatLng" + place.getLatLng() +"\n" + "Address" + place.getAddress()
                        + "Phone" + place.getPhoneNumber());

                entercompanyname.setText(place.getName());
                entercompanystreet.setText(place.getAddress());

                company_Data_Layout.setVisibility(View.VISIBLE);


            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);

                if (status.getStatusMessage() != null)
                {
                    Log.i(TAG, status.getStatusMessage());
                }
            } else if (resultCode == RESULT_CANCELED)
            {

                entercompanyname.setText("");
                entercompanystreet.setText("");

                company_Data_Layout.setVisibility(View.GONE);

            }
        }
    }

    private void initsalaried() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        progressBar = new ProgressDialog(this);
        Select_Place = findViewById(R.id.Search_Place);
        entercompanyname = findViewById(R.id.entercompanyname);
        enterjobrole = findViewById(R.id.enterjobrole);
        enterexp = findViewById(R.id.enterexp);

        entercompanyemail = findViewById(R.id.entercompanyemail);
        entercompanydoorno = findViewById(R.id.entercompanydoorno);
        entercompanystreet = findViewById(R.id.entercompanystreet);
        entercompanycity = findViewById(R.id.entercompanycity);
        entercompanystate = findViewById(R.id.entercompanystate);
        entercompanypincode = findViewById(R.id.entercompanypincode);
        bankstatement_professional = findViewById(R.id.bankstatement_professional);
        company_Data_Layout = findViewById(R.id.company_Data_Layout);
        company_Data_Layout.setVisibility(View.GONE);
        salariedprofsubmit = findViewById(R.id.salariedprofsubmit);
        text_upload_professional = findViewById(R.id.text_upload_professional);
        uploadsuccess_professional = findViewById(R.id.uploadsuccess_professional);

        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        Select_Place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.PHONE_NUMBER,Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        bankstatement_professional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), DocUpload_Professional.class);
                startActivityForResult(intent, GoToDocUpload);

            }
        });

        entercompanyname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterjobrole.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enterexp.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanyemail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanydoorno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0
                        && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanystreet.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanycity.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanystate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        entercompanypincode.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entercompanyname.getText().toString().length() > 0 && enterjobrole.getText().toString().length() > 0 && enterexp.getText().toString().length() > 0 && entercompanyemail.getText().toString().length() > 0
                        && entercompanydoorno.getText().toString().length() > 0
                        && entercompanystreet.getText().toString().length() > 0
                        && entercompanycity.getText().toString().length() > 0
                        && entercompanystate.getText().toString().length() > 0
                        && entercompanypincode.getText().toString().length() > 0 && gotdocuments) {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    status = true;
                } else {
                    salariedprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    status = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        salariedprofsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status) {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    salariedrequest();

                } else {
                    Toasty.info(getApplicationContext(), "please fill the details properly", Toast.LENGTH_SHORT).show();
                }

            }
        });

        enterexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);


                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(ProfessionalInfo.this,
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
                                enterexp.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
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

    }

    private void salariedrequest() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("company_name", entercompanyname.getText().toString());
            jsonObject.put("job_role", enterjobrole.getText().toString());
            jsonObject.put("working_since", enterexp.getText().toString());
            jsonObject.put("company_mail", entercompanyemail.getText().toString());
            jsonObject.put("door_no", entercompanydoorno.getText().toString());
            jsonObject.put("street", entercompanystreet.getText().toString());
            jsonObject.put("city", entercompanycity.getText().toString());
            jsonObject.put("state", entercompanystate.getText().toString());
            jsonObject.put("pincode", entercompanypincode.getText().toString());

            Log.e("salaried", "salariedrequest: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SALARIED_PROF, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    progressBar.cancel();
                    Intent i = new Intent(getApplicationContext(), AddBankDetails.class);
                    startActivity(i);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("eeeeeee", "onErrorResponse: " + error.toString());
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

}
