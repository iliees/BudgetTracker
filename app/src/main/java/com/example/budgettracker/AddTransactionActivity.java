package com.example.budgettracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
            if (!hasPermissions()) {
                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, CAMERA_REQUEST_CODE);
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = getOutputMediaFile();
            if (photoFile != null) {
                imagePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Failed to create file for photo.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Add Transaction button
        buttonAddTransaction.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString();
            String description = editTextDescription.getText().toString();
            String location = editTextLocation.getText().toString();
            int type = radioButtonExpense.isChecked() ? 0 : 1;

            if (amountStr.isEmpty() || description.isEmpty() || location.isEmpty() || imagePath == null) {
                Toast.makeText(this, "Please fill all fields and take a photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.addTransaction(type, amount, description, date, location, imagePath);

            Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }

    private boolean hasPermissions() {
        return checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted. Try taking a photo again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera and storage permissions are required.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getExternalFilesDir(null), "Transactions");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(this, "Failed to create directory.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        try {
            if (mediaFile.createNewFile()) {
                return mediaFile;
            } else {
                Toast.makeText(this, "Failed to create file.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File creation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (imagePath != null) {
                File photoFile = new File(imagePath);
                if (photoFile.exists()) {
                    Toast.makeText(this, "Photo captured successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Photo file not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Photo path is null.", Toast.LENGTH_SHORT).show();
            }
        } else {
            imagePath = null;
            Toast.makeText(this, "Photo capture failed.", Toast.LENGTH_SHORT).show();
        }
    }
}

