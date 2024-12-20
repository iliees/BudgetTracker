package com.example.budgettracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private String imagePath = null;

    private EditText editTextAmount, editTextDescription, editTextLocation;
    private RadioButton radioButtonExpense, radioButtonIncome;
    private Button buttonAddTransaction, buttonTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Initialize views
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLocation = findViewById(R.id.editTextLocation);
        radioButtonExpense = findViewById(R.id.radioButtonExpense);
        radioButtonIncome = findViewById(R.id.radioButtonIncome);
        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
        buttonTakePhoto = findViewById(R.id.buttonTakePhoto);

        // Set up Take Photo button
        buttonTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = getOutputMediaFile();
            if (photoFile != null) {
                imagePath = photoFile.getAbsolutePath();
                Uri photoUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        // Set up Add Transaction button
        buttonAddTransaction.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString();
            String description = editTextDescription.getText().toString();
            String location = editTextLocation.getText().toString();
            int type = radioButtonExpense.isChecked() ? 0 : 1;

            if (amountStr.isEmpty() || description.isEmpty() || location.isEmpty() || imagePath == null) {
                Toast.makeText(AddTransactionActivity.this, "Please fill all fields and take a photo", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            DatabaseHelper dbHelper = new DatabaseHelper(AddTransactionActivity.this);
            dbHelper.addTransaction(type, amount, description, date, location, imagePath);

            Toast.makeText(AddTransactionActivity.this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddTransactionActivity.this, DashboardActivity.class));
            finish();
        });
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getExternalFilesDir(null), "Transactions");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }
}
