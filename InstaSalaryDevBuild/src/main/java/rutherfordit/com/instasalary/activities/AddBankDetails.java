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
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddBankDetails extends AppCompatActivity {

    CardView loader_bankdetails;
    TextInputEditText enterbankname, enterbankbranch, enteraccno, enterbankifsc, enterloanamount, enterloanrepay, enterloanpurpose;
    RelativeLayout bankdetailssubmit;
    ProgressDialog progressBar;
    String UserAccessToken;
    SharedPreferences sharedPreferences;
    boolean click = false;
    TextView error_ifsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_details);

        init();
    }

    private void init() {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        loader_bankdetails = findViewById(R.id.loader_bankdetails);
        progressBar = new ProgressDialog(this);
        enterbankname = findViewById(R.id.enterbankname);
        enterbankbranch = findViewById(R.id.enterbankbranch);
        enteraccno = findViewById(R.id.enteraccno);
        enterbankifsc = findViewById(R.id.enterbankifsc);
        enterbankifsc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11), new InputFilter.AllCaps()});
        bankdetailssubmit = findViewById(R.id.bankdetailssubmit);
        enterloanpurpose = findViewById(R.id.enterloanpurpose);

        enterloanamount = findViewById(R.id.enterloanamount);
        enterloanrepay = findViewById(R.id.enterloanrepay);
        error_ifsc = findViewById(R.id.error_ifsc);

        enterloanpurpose.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11
                        && enterloanpurpose.getText().toString().length() > 1) {
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

        enterbankname.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11
                        && enterloanpurpose.getText().toString().length() > 1) {
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
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11
                        && enterloanpurpose.getText().toString().length() > 1) {
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
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11
                        && enterloanpurpose.getText().toString().length() > 1) {
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

                Pattern pattern = Pattern.compile("[A-Z]{4}[0-9]{7}");

                Matcher matcher = pattern.matcher(s);

                if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                        enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11 && matcher.matches()
                        && enterloanpurpose.getText().toString().length() > 1) {
                    bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                    click = true;
                } else if (enterbankifsc.getText().toString().length() == 11) {
                    if (!matcher.matches()) {
                        error_ifsc.setVisibility(View.VISIBLE);
                        error_ifsc.setText("Enter Correct IFSC code");
                        Toasty.warning(getApplicationContext(), "Enter Correct IFSC code..", Toast.LENGTH_SHORT).show();
                        bankdetailssubmit.setBackgroundColor(Color.parseColor("#36000000"));
                        click = false;
                    } else {
                        error_ifsc.setVisibility(View.GONE);
                        if (enterbankname.getText().toString().length() > 0 && enterbankbranch.getText().toString().length() > 0 &&
                                enteraccno.getText().toString().length() > 9 && enterbankifsc.getText().toString().length() == 11 && matcher.matches()
                                && enterloanpurpose.getText().toString().length() > 1) {
                            bankdetailssubmit.setBackgroundColor(Color.parseColor("#D81B60"));
                            click = true;
                        }
                    }
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

                if (click) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loader_bankdetails.setVisibility(View.VISIBLE);
                    bankdetailsrequest();
                    loandetailsrequest();
                } else {
                    Toasty.warning(getApplicationContext(), "Fill All Details", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loader_bankdetails.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loandetailsrequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", enterloanpurpose.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CREATE_LOAN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("bank_response", "onResponse: " + response);

                if (response.has("data")) {
                    loader_bankdetails.setVisibility(View.GONE);
                    Intent i = new Intent(getApplicationContext(), CreditScoreSctivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    Toasty.success(getApplicationContext(), "Loan Created Successfully..", Toast.LENGTH_SHORT).show();
                } else {
                    loader_bankdetails.setVisibility(View.GONE);
                    Toasty.warning(getApplicationContext(), "Not Saved", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_bankdetails.setVisibility(View.GONE);

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toasty.info(getApplicationContext(), "Please Check the Details You Entered..", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else if (code == 500) {
                    Toasty.warning(getApplicationContext(), "Internal Server Error..", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                //  Log.e(":parama", "getHeaders: " + params );
                return params;
            }
        };

        MySingleton.getInstance(AddBankDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    private void bankdetailsrequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bank_name", enterbankname.getText().toString());
            jsonObject.put("bank_branch", enterbankbranch.getText().toString());
            jsonObject.put("ac_number", enteraccno.getText().toString());
            jsonObject.put("bank_ifcs", enterbankifsc.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.ADD_BANK_DETAILS, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("owner_response", "onResponse: " + response);

                //   loader_bankdetails.setVisibility(View.GONE);

                if (response.has("data")) {
                    Toasty.success(getApplicationContext(), "Saved Bank Details Succesfully..", Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.info(getApplicationContext(), "Data Doesnot Exist..", Toast.LENGTH_SHORT).show();
                    loader_bankdetails.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_bankdetails.setVisibility(View.GONE);

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toasty.info(getApplicationContext(), "Please Check the Details You Entered..", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else if (code == 500) {
                    Toasty.warning(getApplicationContext(), "Internal Server Error..", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

        MySingleton.getInstance(AddBankDetails.this).addToRequestQueue(jsonObjectRequest);

    }

    public void goback(View view) {
        onBackPressed();
    }
}
