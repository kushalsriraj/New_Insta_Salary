package rutherfordit.com.instasalary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;

public class DetailedFragment extends Fragment {

    View v;
    private String detid = "0", UserAccessToken;
    private TextView detail_heading, detail_date, detail_amount, detail_interest, detail_repay_date, detail_status;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_detailed, null);

        init();

        return v;
    }

    private void init() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        detail_heading = v.findViewById(R.id.detail_heading);
        detail_date = v.findViewById(R.id.detail_date);
        detail_amount = v.findViewById(R.id.detail_amount);
        detail_interest = v.findViewById(R.id.detail_interest);
        detail_repay_date = v.findViewById(R.id.detail_repay_date);
        detail_status = v.findViewById(R.id.detail_status);

        request();

        Log.e("detfrag", "init:  " + detid);
    }

    private void request() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("application_id", detid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.LOAN_DETAIL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");

                    detail_heading.setText(data.getString("description"));
                    //    detail_date.setText(data.getString("joined_on"));
                    detail_amount.setText("₹  " + data.getString("amount"));
                    detail_interest.setText(data.getString("intrest") + "%");


                    String date = data.getString("repayable_date");
                    date = date.substring(0, 10);
                    detail_repay_date.setText(date);

                    String datejoined = data.getString("joined_on");
                    datejoined = datejoined.substring(0, 10);
                    detail_date.setText(datejoined);

                    String app_status = data.getString("application_status");

                    if (app_status.equals("0")) {
                        detail_status.setText("Pending");
                        detail_status.setTextColor(getResources().getColor(R.color.white));
                        detail_status.setBackgroundResource(R.color.orange);
                    } else if (app_status.equals("1")) {
                        detail_status.setText("Approved");
                        detail_status.setTextColor(getResources().getColor(R.color.white));
                        detail_status.setBackgroundResource(R.color.blue);
                    } else if (app_status.equals("2")) {
                        detail_status.setText("Completed");
                        detail_status.setTextColor(getResources().getColor(R.color.white));
                        detail_status.setBackgroundResource(R.color.green);
                    } else if (app_status.equals("3")) {
                        detail_status.setText("Rejected");
                        detail_status.setTextColor(getResources().getColor(R.color.white));
                        detail_status.setBackgroundResource(R.color.red);
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void setdata(String id) {
        detid = id;
    }
}
