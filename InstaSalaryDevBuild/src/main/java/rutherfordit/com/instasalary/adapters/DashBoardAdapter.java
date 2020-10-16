package rutherfordit.com.instasalary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.interfaces.LoadDetailedData;
import rutherfordit.com.instasalary.model.LoansModel;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.MyViewHolder> {

    Context context;
    List<LoansModel> models;
    LoadDetailedData loadDetailedData;
    SharedPreferences sharedPreferences;
    String UserAccessToken;

    public DashBoardAdapter(Context context, List<LoansModel> models, LoadDetailedData loadDetailedData) {

        this.context = context;
        this.loadDetailedData = loadDetailedData;
        this.models = models;
        sharedPreferences = context.getSharedPreferences("mySharedPreference", Context.MODE_PRIVATE);
        UserAccessToken = "Bearer " + sharedPreferences.getString("AccessToken", "");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_rec_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.dash_heading.setText(models.get(position).getDesc());
        holder.dash_date.setText(models.get(position).getJoined_on());
        holder.dash_amount.setText("₹  " + models.get(position).getAmount());
        // holder.dash_repay.setText(models.get(position).getRepayable_date());

        String date = models.get(position).getRepayable_date();
        date = date.substring(0, 10);
        holder.dash_repay.setText(date);

        if (models.get(position).getApplication_status().equals("0")) {
            holder.application_status.setText("Pending");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.orange);
        } else if (models.get(position).getApplication_status().equals("1")) {
            holder.application_status.setText("Approved");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.blue);
        } else if (models.get(position).getApplication_status().equals("2")) {
            holder.application_status.setText("Completed");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.green);
        } else if (models.get(position).getApplication_status().equals("3")) {
            holder.application_status.setText("Rejected");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.red);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDetailedData.loaddetails(models.get(position).getId());
            }
        });

        holder.swipe_to_disburse.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                disburseAmount(models.get(position).getId());

            }
        });

    }

    private void disburseAmount(String id)
    {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_disbursed","1");
            jsonObject.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Urls.USER_DISBURSED, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("disburseapi", "onResponse: " + response );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("disburseapi", "onErrorResponse: " + error.getLocalizedMessage() );
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dash_heading, dash_date, dash_amount, dash_repay, application_status;
        SlideToActView swipe_to_disburse;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            swipe_to_disburse = itemView.findViewById(R.id.swipe_to_disburse);
            dash_repay = itemView.findViewById(R.id.dash_repay);
            dash_heading = itemView.findViewById(R.id.dash_heading);
            dash_amount = itemView.findViewById(R.id.dash_amount);
            dash_date = itemView.findViewById(R.id.dash_date);
            application_status = itemView.findViewById(R.id.application_status);

        }
    }
}
