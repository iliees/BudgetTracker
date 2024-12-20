package com.example.budgettracker;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private TextView textViewName, textViewBalance, textViewTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        textViewName = findViewById(R.id.textViewName);
        textViewBalance = findViewById(R.id.textViewBalance);
        textViewTransactions = findViewById(R.id.textViewTransactions);

        // Fetch user details and transactions from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get the first user (modify logic if multiple users are allowed)
        Cursor userCursor = dbHelper.getUser();
        if (userCursor != null && userCursor.moveToFirst()) {
            String name = userCursor.getString(userCursor.getColumnIndex("name"));
            double balance = userCursor.getDouble(userCursor.getColumnIndex("balance"));

            textViewName.setText("Name: " + name);
            textViewBalance.setText("Balance: $" + balance);

            userCursor.close();
        }

        // Get the last 5 transactions
        Cursor transactionCursor = dbHelper.getLastTransactions(5);  // Method to fetch last 5 transactions
        ArrayList<String> transactions = new ArrayList<>();

        if (transactionCursor != null) {
            while (transactionCursor.moveToNext()) {
                String type = transactionCursor.getInt(transactionCursor.getColumnIndex("type")) == 0 ? "Expense" : "Income";
                double amount = transactionCursor.getDouble(transactionCursor.getColumnIndex("amount"));
                String date = transactionCursor.getString(transactionCursor.getColumnIndex("transaction_date"));
                String description = transactionCursor.getString(transactionCursor.getColumnIndex("description"));

                transactions.add(type + ": $" + amount + " on " + date + " (" + description + ")");
            }
            transactionCursor.close();
        }

        // Display transactions
        if (transactions.isEmpty()) {
            textViewTransactions.setText("No recent transactions found.");
        } else {
            StringBuilder transactionText = new StringBuilder("Last 5 Transactions:\n");
            for (String transaction : transactions) {
                transactionText.append(transaction).append("\n");
            }
            textViewTransactions.setText(transactionText.toString());
        }
    }
}
