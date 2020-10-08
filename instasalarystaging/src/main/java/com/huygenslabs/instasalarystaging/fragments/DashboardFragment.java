package com.huygenslabs.instasalarystaging.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.activities.MainActivity;
import com.huygenslabs.instasalarystaging.adapters.DashBoardAdapter;
import com.huygenslabs.instasalarystaging.adapters.LoansAdapter;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.huygenslabs.instasalarystaging.interfaces.LoadDetailedData;
import com.huygenslabs.instasalarystaging.model.LoansModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {

    View v;
    RecyclerView recdashboard;
    List<LoansModel> models;
    private String UserAccessToken;
    TextView emptydash;
    ProgressDialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_dashboard, null);

        init();

        return v;
    }

    private void init() {

        progressBar = new ProgressDialog(getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        models = new ArrayList<>();

        recdashboard = v.findViewById(R.id.recdashboard);
        emptydash = v.findViewById(R.id.emptydash);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recdashboard.setLayoutManager(linearLayoutManager);

        request();

    }

    private void request() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.LOANS_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    if (jsonArray.length() > 0)
                    {

                        emptydash.setVisibility(View.GONE);

                        for (int i = 0 ; i < jsonArray.length() ; i++ )
                        {

                            JSONObject object = jsonArray.getJSONObject(i);

                            String status = object.getString("application_status");

                            if (status.equals("0") || status.equals("1"))
                            {

                                Log.e("stauts", "onResponse: " + status );

                                LoansModel data = new LoansModel();

                                data.setAmount(object.getString("amount"));
                                data.setDesc(object.getString("description"));
                                data.setId(object.getString("id"));
                                data.setIntrest(object.getString("intrest"));
                                data.setJoined_on(object.getString("joined_on_human"));
                                data.setRepayable_date(object.getString("repayable_date"));
                                data.setUser_id(object.getString("user_id"));
                                data.setApplication_status(object.getString("application_status"));

                                models.add(data);
                                progressBar.cancel();
                            }
                            else
                            {
                                emptydash.setVisibility(View.GONE);
                                progressBar.cancel();
                            }

                        }

                        DashBoardAdapter dashBoardAdapter = new DashBoardAdapter(getContext(), models, (MainActivity) getActivity());
                        recdashboard.setAdapter(dashBoardAdapter);
                    }

                    else if (jsonArray.length() == 0)
                    {
                        emptydash.setVisibility(View.VISIBLE);
                        progressBar.cancel();
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

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

}
