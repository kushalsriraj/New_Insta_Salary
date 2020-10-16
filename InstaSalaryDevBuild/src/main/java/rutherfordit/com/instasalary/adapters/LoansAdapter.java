package rutherfordit.com.instasalary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.model.LoansModel;

public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.MyViewHolder> {

    Context context;
    List<LoansModel> models;

    public LoansAdapter(Context context, List<LoansModel> models) {

        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loans_rec_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.loan_heading.setText(models.get(position).getDesc());
        //  holder.loan_date.setText(models.get(position).getRepayable_date());
        holder.loan_amount.setText("₹  " + models.get(position).getAmount());

        String date = models.get(position).getRepayable_date();
        date = date.substring(0, 10);
        holder.loan_date.setText(date);

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

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView loan_heading, loan_date, loan_amount, application_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            loan_heading = itemView.findViewById(R.id.loan_heading);
            loan_date = itemView.findViewById(R.id.loan_date);
            loan_amount = itemView.findViewById(R.id.loan_amount);
            application_status = itemView.findViewById(R.id.loan_status);

        }
    }
}
