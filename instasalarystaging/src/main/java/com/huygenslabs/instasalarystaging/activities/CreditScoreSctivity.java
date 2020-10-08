package com.huygenslabs.instasalarystaging.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class CreditScoreSctivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String UserAccessToken;
    //ArcProgress progress;
    int creditscore;
    String loanamount, maxscore, id;
    TextView amount;
    RelativeLayout next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_score_sctivity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this ,R.color.creditbg));
        }

        init();
    }

    private void init()
    {

        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
        //progress = findViewById(R.id.arc_progress1);
        amount = findViewById(R.id.amount);
        next = findViewById(R.id.addbankdetails);

        Log.e("credit", "init: " + UserAccessToken );

      //  request();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });



    }

    private void request()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.CREDIT_SCORE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("credit", "onResponse: " + response );

                try {

                    JSONObject data = response.getJSONObject("data");

                    if(data.length()==0)
                    {
                        Toast.makeText(getApplicationContext(),"Credit Score Not Built",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        creditscore = data.getInt("credit_score");
                        loanamount = data.getString("loan_amount");
                        id = data.getString("id");

                        //progress.setProgress(creditscore);
                        amount.setText(loanamount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        MySingleton.getInstance(CreditScoreSctivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void gotomain(View view)
    {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
}
