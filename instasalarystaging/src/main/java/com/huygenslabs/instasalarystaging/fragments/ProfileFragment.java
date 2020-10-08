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

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.huygenslabs.instasalarystaging.R;
import com.huygenslabs.instasalarystaging.activities.MainActivity;
import com.huygenslabs.instasalarystaging.adapters.DashBoardAdapter;
import com.huygenslabs.instasalarystaging.extras.MySingleton;
import com.huygenslabs.instasalarystaging.extras.Urls;
import com.huygenslabs.instasalarystaging.model.LoansModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    ProgressDialog progressBar;
    CircleImageView img;
    View v;
    SharedPreferences sharedPreferences;
    String UserAccessToken;
    TextView profile_doorno,profile_street,profile_state,profile_city,
            curr_profile_doorno, curr_profile_street,curr_profile_state,curr_profile_city,profile_c_name,profile_c_role,profile_c_joining,profile_c_email;
    TextView profile_fullname,profile_email,profile_phone,profile_aadhar,profile_pan,profile_dob;
    JSONObject data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_profile, null);

        init();

        return v;
    }

    private void init() {

        progressBar = new ProgressDialog(getContext());
        sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait..");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        if (progressBar.isShowing())
        {
            img = v.findViewById(R.id.img);
            profile_fullname = v.findViewById(R.id.profile_fullname);
            profile_email = v.findViewById(R.id.profile_email);
            profile_phone = v.findViewById(R.id.profile_phone);
            profile_aadhar = v.findViewById(R.id.profile_aadhar);
            profile_pan = v.findViewById(R.id.profile_pan);
            profile_dob = v.findViewById(R.id.profile_dob);
            profile_doorno = v.findViewById(R.id.profile_doorno);
            profile_street = v.findViewById(R.id.profile_street);
            profile_state = v.findViewById(R.id.profile_state);
            profile_city = v.findViewById(R.id.profile_city);

            curr_profile_doorno = v.findViewById(R.id.curr_profile_doorno);
            curr_profile_street = v.findViewById(R.id.curr_profile_street);
            curr_profile_state = v.findViewById(R.id.curr_profile_state);
            curr_profile_city = v.findViewById(R.id.curr_profile_city);
            profile_c_name = v.findViewById(R.id.profile_c_name);
            profile_c_role = v.findViewById(R.id.profile_c_role);
            profile_c_joining = v.findViewById(R.id.profile_c_joining);
            profile_c_email = v.findViewById(R.id.profile_c_email);

            req();
        }

      //  req();

      //  request();
    }

    private void req()
    {
        Log.e("obj", "senddata: " + data );

        try {
            profile_fullname.setText(data.getString("name"));
            profile_email.setText(data.getString("email"));
            profile_phone.setText(data.getString("phone_number"));
            profile_aadhar.setText(data.getString("aadhar_number"));
            profile_pan.setText(data.getString("pan_number"));
            profile_dob.setText(data.getString("dob"));

            JSONArray address_array = data.getJSONArray("addressdetails");

            if (address_array.length() > 0)
            {
                for(int j = 0 ; j < address_array.length() ; j++)
                {

                    progressBar.cancel();

                }
            }
            else
            {
                progressBar.cancel();
            }

            JSONObject imagedetails = data.getJSONObject("imagedetails");

            JSONArray imagedata = imagedetails.getJSONArray("data");

            if (imagedata.length() > 0)
            {
                for(int j = 0 ; j < imagedata.length() ; j++)
                {

                    String image = imagedata.getJSONObject(0).getString("proof");

                    Picasso.with(getContext()).load(Urls.IMAGE_CONSTANT+image).into(img);

                    progressBar.cancel();

                }
            }
            else
            {
                progressBar.cancel();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.cancel();
        }
    }

    /*private void request()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.USER_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject data = response.getJSONObject("data");

                    profile_fullname.setText(data.getString("name"));
                    profile_email.setText(data.getString("email"));
                    profile_phone.setText(data.getString("phone_number"));
                    profile_aadhar.setText(data.getString("aadhar_number"));
                    profile_pan.setText(data.getString("pan_number"));
                    profile_dob.setText(data.getString("dob"));

                    JSONObject imagedetails = data.getJSONObject("imagedetails");

                    JSONArray imagedata = imagedetails.getJSONArray("data");

                    for(int j = 0 ; j < imagedata.length() ; j++)
                    {

                        String image = imagedata.getJSONObject(0).getString("proof");

                        Picasso.with(getContext()).load(Urls.IMAGE_CONSTANT+image).into(img);

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

    }*/

    public void senddata(JSONObject mydata) {

        data = mydata;

       // init();
    }
}
