package rutherfordit.com.instasalary.activities;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.model.VendorModel;

public class DriverProfessionalInfo extends AppCompatActivity {

    String Id = "";
    TextInputEditText entervehicleno, entervendor, enteravgincome;
    RelativeLayout ownerprofsubmit;
    boolean click = false;
    Spinner vendorspinner;
    ProgressDialog progressBar;
    Button upload_driving_licence;
    boolean gotdocuments = false;
    TextView text_upload_driver;
    ImageView uploadsuccess_driver;
    SharedPreferences sharedPreferences;
    String UserAccessToken, Vendor;
    String message = "";
    TextView error_veh_no;
    private ArrayList<VendorModel> vendorModelArrayList;
    private ArrayList<String> names, ids;
    private int GoToDocUpload = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_professional_info);

        initowner();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoToDocUpload) {

            assert data != null;
            message = data.getStringExtra("MESSAGE");

            assert message != null;
            if (message.equals("Success")) {
                gotdocuments = true;
                click = true;

                if (error_veh_no.getVisibility() == View.VISIBLE) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    uploadsuccess_driver.setVisibility(View.VISIBLE);
                    text_upload_driver.setText("Documents Uploaded");
                    upload_driving_licence.setVisibility(View.GONE);
                    click = false;
                } else if (error_veh_no.getVisibility() == View.GONE && entervendor.getText().toString().length() > 0 &&
                        enteravgincome.getText().toString().length() > 0 && !Vendor.equals("-- Select Vendor --")) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    uploadsuccess_driver.setVisibility(View.VISIBLE);
                    text_upload_driver.setText("Documents Uploaded");
                    upload_driving_licence.setVisibility(View.GONE);
                    click = true;
                } else {
                    uploadsuccess_driver.setVisibility(View.VISIBLE);
                    text_upload_driver.setText("Documents Uploaded");
                    upload_driving_licence.setVisibility(View.GONE);
                    click = true;
                }
            }

            Log.e("driverprof", "onActivityResult:  " + message);

        }
    }


    private void initowner() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        progressBar = new ProgressDialog(this);
        entervehicleno = findViewById(R.id.entervehicleno);
        entervehicleno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), new InputFilter.AllCaps()});
        entervendor = findViewById(R.id.entervendor);
        enteravgincome = findViewById(R.id.enteravgincome);
        ownerprofsubmit = findViewById(R.id.ownerprofsubmit);
        vendorspinner = findViewById(R.id.vendorspinner);
        vendorModelArrayList = new ArrayList<>();
        names = new ArrayList<String>();
        names.add("-- Select Vendor --");
        ids = new ArrayList<String>();
        upload_driving_licence = findViewById(R.id.upload_driving_licence);
        text_upload_driver = findViewById(R.id.text_upload_driver);
        uploadsuccess_driver = findViewById(R.id.uploadsuccess_driver);
        error_veh_no = findViewById(R.id.error_veh_no);

        requestvendordata();

        entervendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorspinner.performClick();
            }
        });

        vendorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                Vendor = parent.getItemAtPosition(pos).toString();

                if (!Vendor.equals("-- Select Vendor --")) {

                    if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                            enteravgincome.getText().toString().length() > 4 && !Vendor.equals("-- Select Vendor --") && gotdocuments) {
                        ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        click = true;
                    } else {
                        ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                        click = false;
                    }

                    Id = vendorModelArrayList.get(pos - 1).getId();
                } else {

                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }

                entervendor.setText(Vendor);
                entervendor.setCursorVisible(false);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        entervehicleno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Pattern pattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}");

                Matcher matcher = pattern.matcher(s);

                if (error_veh_no.getVisibility() == View.GONE && entervendor.getText().toString().length() > 0 &&
                        enteravgincome.getText().toString().length() > 4 && !Vendor.equals("-- Select Vendor --") && matcher.matches() && gotdocuments) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else if (entervehicleno.getText().toString().length() == 10) {
                    if (!matcher.matches()) {
                        error_veh_no.setVisibility(View.VISIBLE);
                        error_veh_no.setText("Enter Correct Vehicle Number");
                        Toast.makeText(getApplicationContext(), "Enter Correct Vehicle Number..", Toast.LENGTH_SHORT).show();
                        ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                        click = false;
                    } else {
                        error_veh_no.setVisibility(View.GONE);
                        ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                        click = true;
                    }
                } else {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        enteravgincome.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (entervehicleno.getText().toString().length() > 0 && entervendor.getText().toString().length() > 0 &&
                        enteravgincome.getText().toString().length() > 0 && !Vendor.equals("-- Select Vendor --") && gotdocuments) {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else {
                    ownerprofsubmit.setBackgroundColor(Color.parseColor("#36000000"));
                    click = false;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        ownerprofsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click) {
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Please Wait..");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    ownerrequest();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Check All The fields..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload_driving_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DocUpload_Driver.class);
                startActivityForResult(intent, GoToDocUpload);
            }
        });

    }

    private void requestvendordata() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.VENDORS_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray data = response.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {

                        JSONObject dataobj = data.getJSONObject(i);

                        VendorModel Model = new VendorModel();

                        Model.setId(dataobj.getString("id"));
                        Model.setName(dataobj.getString("name"));
                        Model.setPhone(dataobj.getString("phone_number"));
                        Model.setAddress(dataobj.getString("address"));
                        Model.setJoinedon(dataobj.getString("joined_on"));

                        vendorModelArrayList.add(Model);

                        names.add(dataobj.getString("name"));

                        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, names);

                        vendorspinner.setAdapter(aa);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        MySingleton.getInstance(DriverProfessionalInfo.this).addToRequestQueue(jsonObjectRequest);

    }

    private void ownerrequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vehicle_number", entervehicleno.getText().toString());
            jsonObject.put("vendor_id", Id);
            jsonObject.put("avg_income", enteravgincome.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("obj", "ownerrequest: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.OWNER_PROF, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("owner_response", "onResponse: " + response);

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
                Log.e(":parama", "getHeaders: " + params);
                return params;
            }
        };

        MySingleton.getInstance(DriverProfessionalInfo.this).addToRequestQueue(jsonObjectRequest);
    }

}