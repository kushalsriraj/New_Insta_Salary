package rutherfordit.com.instasalary.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.Urls;


public class ProfessionalInfo extends AppCompatActivity {

    List<Address> addresses;
    Geocoder geocoder;
    PlacesClient placesClient;
    RecyclerView rec_googleplaces;
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
    TextView text_upload_professional,re_search;
    ImageView uploadsuccess_professional;
    private int GoToDocUpload = 1000;
    TextInputLayout tip_search;

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
                Select_Place.setVisibility(View.GONE);
                tip_search.setVisibility(View.GONE);
                re_search.setVisibility(View.VISIBLE);
               // Select_Place.setText("Name : " + place.getName() + "\n" + "LatLng" + place.getLatLng() +"\n" + "Address" + place.getAddress()+ "Phone" + place.getPhoneNumber());

                entercompanyname.setText(place.getName());
                entercompanystreet.setText(place.getAddress());

                String latlng = String.valueOf(place.getLatLng());

                latlng = latlng.replace("(", "");
                latlng = latlng.replace(")", "");
                latlng = latlng.replace("lat/lng: ", "");

                String[] namesList = latlng.split(",");

                String lat = namesList[0];
                String longi = namesList[1];

                Log.e(TAG, "lat " + lat + " long " + longi);


                double la=Double.parseDouble(lat.toString());
                double lo=Double.parseDouble(longi.toString());

                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(la, lo, 1);

                    String postalCode = addresses.get(0).getPostalCode();
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();

                    entercompanypincode.setText(postalCode);
                    entercompanystate.setText(state);
                    entercompanycity.setText(city);

                    Log.e(TAG, "onActivityResult: " + postalCode + " " + city+ " " + state );
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        rec_googleplaces = findViewById(R.id.rec_googleplaces);
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
        tip_search = findViewById(R.id.tip_search);
        re_search = findViewById(R.id.re_search);
        re_search.setVisibility(View.GONE);

        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);

        Select_Place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.PHONE_NUMBER,Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        re_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tip_search.setVisibility(View.VISIBLE);
                Select_Place.setText("");
                Select_Place.setVisibility(View.VISIBLE);
                company_Data_Layout.setVisibility(View.GONE);
                re_search.setVisibility(View.GONE);
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

    private void getlocations(String searching) {

        Toast.makeText(ProfessionalInfo.this, searching, Toast.LENGTH_SHORT).show();
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
              //  .setLocationRestriction(bounds)
                .setCountry("in")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(searching)
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            StringBuilder mResult = new StringBuilder();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                mResult.append(" ").append(prediction.getFullText(null) + "\n");
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
                Toast.makeText(ProfessionalInfo.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "getlocations: " + mResult );
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
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
