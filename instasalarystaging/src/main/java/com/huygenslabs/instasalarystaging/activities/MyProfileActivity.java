package com.huygenslabs.instasalarystaging.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String UserAccessToken;
    TextView user_fullname, user_email, user_phoneno, user_aadhar, user_pan, user_vehicleno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);

        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        user_fullname = findViewById(R.id.user_fullname);
        user_email = findViewById(R.id.user_email);
        user_phoneno = findViewById(R.id.user_phoneno);
        user_aadhar = findViewById(R.id.user_aadhar);
        user_pan = findViewById(R.id.user_pan);
        user_vehicleno = findViewById(R.id.user_vehicleno);

        request();

        //Log.e("Token", "init: " + UserAccessToken );
    }

    private void request() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.USER_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    user_fullname.setText(response.getString("name"));
                    user_email.setText(response.getString("email"));
                    user_phoneno.setText(response.getString("phone_number"));
                    user_aadhar.setText(response.getString("aadhar_number"));
                    user_pan.setText(response.getString("pan_number"));

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

        MySingleton.getInstance(MyProfileActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}
