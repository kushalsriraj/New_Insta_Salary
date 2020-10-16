package rutherfordit.com.instasalary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rutherfordit.com.instasalary.R;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {

    Context context;
    ArrayList questions;
    ArrayList answers;

    public FaqAdapter(Context context, ArrayList questions, ArrayList answers) {

        this.answers = answers;
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_rec_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.faqquestions.setText((CharSequence) questions.get(position));

        holder.faqanswers.setVisibility(View.GONE);

        holder.faqquestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.faqanswers.getVisibility() == View.GONE) {
                    holder.faqquestions.setTextColor(ContextCompat.getColor(context, R.color.instapink));
                    holder.faqanswers.setVisibility(View.VISIBLE);
                    holder.faqanswers.setText((CharSequence) answers.get(position));
                } else {
                    holder.faqquestions.setTextColor(ContextCompat.getColor(context, R.color.black));
                    holder.faqanswers.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView faqquestions, faqanswers;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            faqanswers = itemView.findViewById(R.id.faqanswer);
            faqquestions = itemView.findViewById(R.id.faqquestion);

        }
    }
}
