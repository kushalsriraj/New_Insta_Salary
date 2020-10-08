package rutherfordit.com.instasalary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.extras.MySingleton;
import rutherfordit.com.instasalary.extras.Urls;
import rutherfordit.com.instasalary.interfaces.LoadDetailedData;
import rutherfordit.com.instasalary.model.LoansModel;

import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.MyViewHolder> {

    Context context;
    List<LoansModel> models;
    LoadDetailedData loadDetailedData;

    public DashBoardAdapter(Context context, List<LoansModel> models, LoadDetailedData loadDetailedData) {

        this.context = context;
        this.loadDetailedData = loadDetailedData;
        this.models = models;
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
        date = date.substring(0,10);
        holder.dash_repay.setText(date);

        if (models.get(position).getApplication_status().equals("0"))
        {
            holder.application_status.setText("Pending");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.orange);
        }
        else if (models.get(position).getApplication_status().equals("1"))
        {
            holder.application_status.setText("Approved");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.blue);
        }
        else if (models.get(position).getApplication_status().equals("2"))
        {
            holder.application_status.setText("Completed");
            holder.application_status.setTextColor(context.getResources().getColor(R.color.white));
            holder.application_status.setBackgroundResource(R.color.green);
        }
        else if (models.get(position).getApplication_status().equals("3"))
        {
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

    }

    @Override
    public int getItemCount()
    {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dash_heading, dash_date, dash_amount, dash_repay, application_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dash_repay = itemView.findViewById(R.id.dash_repay);
            dash_heading = itemView.findViewById(R.id.dash_heading);
            dash_amount = itemView.findViewById(R.id.dash_amount);
            dash_date = itemView.findViewById(R.id.dash_date);
            application_status = itemView.findViewById(R.id.application_status);

        }
    }
}
