package com.example.bankly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankly.Models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        try {
            String rawDate = transaction.getDateTime();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            Date parsedDate = inputFormat.parse(rawDate);
            holder.tvDate.setText(outputFormat.format(parsedDate));
        }
        catch (Exception e) {
            holder.tvDate.setText("Unknown Date");
        }

        String type = transaction.getType();

        if ("DEPOSIT".equalsIgnoreCase(type)) {
            holder.ivStatusIcon.setImageResource(android.R.drawable.arrow_down_float);
        }
        else if ("WITHDRAWAL".equalsIgnoreCase(type)) {
            holder.ivStatusIcon.setImageResource(android.R.drawable.arrow_up_float);
        }
        else {
            holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_info_details);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransactionDetailActivity.class);
            intent.putExtra("transaction", transaction);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactionList.clear();
        this.transactionList.addAll(newTransactions);
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        ImageView ivStatusIcon;
        TextView tvDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}