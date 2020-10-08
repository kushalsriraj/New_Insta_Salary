package rutherfordit.com.instasalary.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SegmentActivity extends AppCompatActivity {

    RadioButton salariedradio, selfempradio, driverradio, ownerradio;
    RadioGroup secondradiogroup;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    int value;
    RelativeLayout SegmentsubmitButton;
    Boolean click = false;
    CardView loader_segment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment);

        init();

    }

    private void init() {

        loader_segment = findViewById(R.id.loader_segment);
        loader_segment.setVisibility(View.GONE);
        sharedPreferences = getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        SegmentsubmitButton = findViewById(R.id.SegmentsubmitButton);
        secondradiogroup = findViewById(R.id.segmentradiogroup);
        salariedradio = findViewById(R.id.salariedradioButton);
        selfempradio = findViewById(R.id.selfempradioButton);
        driverradio = findViewById(R.id.driverradioButton);
        ownerradio = findViewById(R.id.ownerradioButton);
        ownerradio.setVisibility(View.GONE);

        salariedradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SegmentsubmitButton.setBackgroundColor(Color.parseColor("#D81B60"));
                click = true;
                value = 1;
                secondradiogroup.setVisibility(View.GONE);
                ownerradio.setChecked(false);
            }
        });

        selfempradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SegmentsubmitButton.setBackgroundColor(Color.parseColor("#36000000"));
                secondradiogroup.setVisibility(View.VISIBLE);
                click = false;
                ownerradio.setVisibility(View.VISIBLE);
            }
        });

        ownerradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = 2;
                click = true;
                SegmentsubmitButton.setBackgroundColor(Color.parseColor("#D81B60"));
            }
        });

        SegmentsubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (value == 1)
                    {
                        callintent("salaried");
                        editor.putString("segment_value", "salaried");
                        editor.apply();
                    }
                    else if(value == 2)
                    {
                        callintent("owner");
                        editor.putString("segment_value", "owner");
                        editor.apply();
                    }

                }
            }

            private void callintent(String type)
            {
                loader_segment.setVisibility(View.VISIBLE);
                segmentrequest(String.valueOf(value),type);
            }
        });

    }

    private void
    segmentrequest(String value, final String type) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("segment", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("eerroorr", "segmentrequest: " + jsonObject );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.SEND_SEGMENT, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), PersonalInfo.class);
                   // i.putExtra("sendfromsignup",type);
                    loader_segment.setVisibility(View.GONE);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("eerroorr", "onErrorResponse: " + error.toString() );
                loader_segment.setVisibility(View.GONE);
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

        MySingleton.getInstance(SegmentActivity.this).addToRequestQueue(jsonObjectRequest);

    }
}