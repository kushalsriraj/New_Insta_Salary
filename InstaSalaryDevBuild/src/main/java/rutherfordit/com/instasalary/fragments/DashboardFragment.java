package rutherfordit.com.instasalary.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.activities.MainActivity;
import rutherfordit.com.instasalary.adapters.DashBoardAdapter;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.model.LoansModel;

public class DashboardFragment extends Fragment {

    View v;
    RecyclerView recdashboard;
    List<LoansModel> models;
    TextView emptydash;
    RelativeLayout apply_new_Loan;
    EditText enter_purpose;
    ImageView cancel_dialog;
    Button apply_for_loan;
    LinearLayout application_success, application_failure;
    DashBoardAdapter dashBoardAdapter;
    CardView loader_dashboard;
    private String UserAccessToken;

    /*@Override
    public void onResume() {
        super.onResume();
        init();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_dashboard, null);

        init();

        return v;
    }

    private void init() {

        loader_dashboard = v.findViewById(R.id.loader_dashboard);
        loader_dashboard.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        Log.e("mytoken", "init: " + UserAccessToken);

        models = new ArrayList<>();

        apply_new_Loan = v.findViewById(R.id.apply_new_Loan);
        recdashboard = v.findViewById(R.id.recdashboard);
        emptydash = v.findViewById(R.id.emptydash);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recdashboard.setLayoutManager(linearLayoutManager);

        request();

        apply_new_Loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });

    }

    private void showdialog() {

        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
        enter_purpose = dialog.findViewById(R.id.enter_purpose);
        apply_for_loan = dialog.findViewById(R.id.apply_for_loan);
        application_failure = dialog.findViewById(R.id.application_failure);
        application_success = dialog.findViewById(R.id.application_success);

        apply_for_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyloan();
            }
        });

        dialog.show();

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (application_success.getVisibility() == View.VISIBLE) {
                    dialog.cancel();
                    request();
                } else if (application_success.getVisibility() == View.GONE) {
                    dialog.cancel();
                }
            }
        });


    }

    private void applyloan() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("description", enter_purpose.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.CREATE_LOAN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("bank_response", "onResponse: " + response);

                if (response.has("data")) {
                    Toast.makeText(getContext(), "Loan Applied..", Toast.LENGTH_SHORT).show();
                    application_success.setVisibility(View.VISIBLE);
                    application_failure.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_dashboard.setVisibility(View.GONE);

                int code = error.networkResponse.statusCode;

                if (code == 422) {
                    Toast.makeText(getContext(), "Please Check the Details You Entered..", Toast.LENGTH_SHORT).show();
                } else if (code == 500) {
                    Toast.makeText(getContext(), "Internal Server Error..", Toast.LENGTH_SHORT).show();
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

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }

    private void request() {

        models.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.LOANS_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    if (jsonArray.length() > 0) {

                        emptydash.setVisibility(View.GONE);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            String status = object.getString("application_status");

                            if (status.equals("0") || status.equals("1")) {

                                Log.e("stauts", "onResponse: " + status);

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
                                loader_dashboard.setVisibility(View.GONE);
                            } else {
                                emptydash.setVisibility(View.GONE);
                                loader_dashboard.setVisibility(View.GONE);
                            }

                        }

                        dashBoardAdapter = new DashBoardAdapter(getContext(), models, (MainActivity) getActivity());
                        recdashboard.setAdapter(dashBoardAdapter);
                        dashBoardAdapter.notifyDataSetChanged();
                    } else if (jsonArray.length() == 0) {
                        emptydash.setVisibility(View.VISIBLE);
                        loader_dashboard.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader_dashboard.setVisibility(View.VISIBLE);

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

}
