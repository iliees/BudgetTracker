package com.example.budgettracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private TextView textViewName, textViewBalance;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;
    private ArrayList<String> transactions;
    private Button buttonAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        textViewName = findViewById(R.id.textViewName);
        textViewBalance = findViewById(R.id.textViewBalance);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);

        // Set up RecyclerView
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        recyclerViewTransactions.setAdapter(transactionAdapter);

        // Fetch user details and transactions from the database
        loadUserData();
        loadTransactions();

        // Set up Add Transaction button listener
        buttonAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor userCursor = dbHelper.getUser();

        if (userCursor != null && userCursor.moveToFirst()) {
            String name = userCursor.getString(userCursor.getColumnIndex("name"));
            double balance = userCursor.getDouble(userCursor.getColumnIndex("balance"));

            textViewName.setText("Name: " + name);
            textViewBalance.setText("Balance: $" + balance);

            userCursor.close();
        }
    }

    private void loadTransactions() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor transactionCursor = dbHelper.getLastTransactions(5);

        if (transactionCursor != null) {
            transactions.clear();
            while (transactionCursor.moveToNext()) {
                String type = transactionCursor.getInt(transactionCursor.getColumnIndex("type")) == 0 ? "Expense" : "Income";
                double amount = transactionCursor.getDouble(transactionCursor.getColumnIndex("amount"));
                String date = transactionCursor.getString(transactionCursor.getColumnIndex("transaction_date"));
                String description = transactionCursor.getString(transactionCursor.getColumnIndex("description"));

                transactions.add(type + ": $" + amount + " on " + date + " (" + description + ")");
            }
            transactionCursor.close();
        }

        transactionAdapter.notifyDataSetChanged();
    }

    // Simple Adapter to display transactions in RecyclerView
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        private ArrayList<String> transactions;

        public TransactionAdapter(ArrayList<String> transactions) {
            this.transactions = transactions;
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            String transaction = transactions.get(position);
            holder.transactionText.setText(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public class TransactionViewHolder extends RecyclerView.ViewHolder {
            TextView transactionText;

            public TransactionViewHolder(View itemView) {
                super(itemView);
                transactionText = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
