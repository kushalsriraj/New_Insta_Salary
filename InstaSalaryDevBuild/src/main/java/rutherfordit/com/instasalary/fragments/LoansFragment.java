package rutherfordit.com.instasalary.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import rutherfordit.com.instasalary.adapters.LoansAdapter;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.model.LoansModel;

public class LoansFragment extends Fragment {

    View v;
    List<LoansModel> model;
    TextView emptyloanfrag;
    CardView loader_loans;
    private String UserAccessToken;
    private RecyclerView recloans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_loans, null);

        init();

        return v;
    }

    private void init() {

        loader_loans = v.findViewById(R.id.loader_loans);
        loader_loans.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");

        model = new ArrayList<>();

        recloans = v.findViewById(R.id.recloans);
        emptyloanfrag = v.findViewById(R.id.emptyloanfrag);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recloans.setLayoutManager(linearLayoutManager);

        request();

    }

    private void request() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Urls.LOANS_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    if (jsonArray.length() > 0) {

                        emptyloanfrag.setVisibility(View.GONE);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            LoansModel data = new LoansModel();

                            data.setAmount(object.getString("amount"));
                            data.setDesc(object.getString("description"));
                            data.setId(object.getString("id"));
                            data.setIntrest(object.getString("intrest"));
                            data.setJoined_on(object.getString("joined_on_human"));
                            data.setRepayable_date(object.getString("repayable_date"));
                            data.setUser_id(object.getString("user_id"));
                            data.setApplication_status(object.getString("application_status"));

                            model.add(data);
                            loader_loans.setVisibility(View.GONE);
                        }

                        LoansAdapter loansAdapter = new LoansAdapter(getContext(), model);
                        recloans.setAdapter(loansAdapter);
                    } else if (jsonArray.length() == 0) {
                        emptyloanfrag.setVisibility(View.VISIBLE);
                        loader_loans.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loader_loans.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader_loans.setVisibility(View.GONE);
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

