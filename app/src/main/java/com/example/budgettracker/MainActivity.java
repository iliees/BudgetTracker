package com.example.budgettracker;

import android.content.Intent; // Import Intent
import android.content.SharedPreferences; // Import SharedPreferences
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure edge-to-edge behavior (if needed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check SharedPreferences for the first run
        SharedPreferences preferences = getSharedPreferences("BudgetTrackerPrefs", MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            // Redirect to ParameterActivity
            Intent intent = new Intent(MainActivity.this, ParameterActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
        } else {
            // Redirect to DashboardActivity (or another main activity)
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
