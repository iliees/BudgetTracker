package com.example.budgettracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ParameterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextBalance;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextBalance = findViewById(R.id.editTextBalance);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                double balance = Double.parseDouble(editTextBalance.getText().toString());

                // Save to database
                DatabaseHelper dbHelper = new DatabaseHelper(ParameterActivity.this);
                dbHelper.addUser(name, email, balance); // Implement this in your DatabaseHelper

                // Update SharedPreferences
                SharedPreferences preferences = getSharedPreferences("BudgetTrackerPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isFirstRun", false); // Mark the app as no longer in first run
                editor.apply();

                // Redirect to DashboardActivity
                Intent intent = new Intent(ParameterActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

