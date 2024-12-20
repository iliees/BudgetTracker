package com.example.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "BudgetTracker.db";
    private static final int DATABASE_VERSION = 2; // Incremented for schema change

    // Table User
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "User_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_BALANCE = "balance";

    // Table Transaction
    private static final String TABLE_TRANSACTION = "`Transaction`"; // Use backticks to escape
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_TYPE = "type"; // Bool (0 for expense, 1 for income)
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "transaction_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location"; // For transaction location
    private static final String COLUMN_IMAGE_PATH = "image_path"; // New column for image

    // Table RegularTransaction
    private static final String TABLE_REGULAR_TRANSACTION = "RegularTransaction";
    private static final String COLUMN_LAST_CHECK = "lastCheck";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_BALANCE + " REAL"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        // Create Transaction Table
        String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TYPE + " INTEGER, " // 0 for expense, 1 for income
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_IMAGE_PATH + " TEXT" // Add image path column here
                + ")";
        db.execSQL(CREATE_TRANSACTION_TABLE);

        // Create RegularTransaction Table
        String CREATE_REGULAR_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_REGULAR_TRANSACTION + "("
                + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_LAST_CHECK + " TEXT, "
                + "FOREIGN KEY (" + COLUMN_TRANSACTION_ID + ") REFERENCES " + TABLE_TRANSACTION + "(" + COLUMN_TRANSACTION_ID + ")"
                + ")";
        db.execSQL(CREATE_REGULAR_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TRANSACTION + " ADD COLUMN " + COLUMN_IMAGE_PATH + " TEXT");
        }
    }

    // Add transaction with image and update balance
    public void addTransaction(int type, double amount, String description, String date, String location, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert transaction
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_IMAGE_PATH, imagePath); // Save image path

        db.insert(TABLE_TRANSACTION, null, values);

        // Update user balance
        updateUserBalance(type, amount);

        db.close();
    }

    // Update user balance based on transaction type
    private void updateUserBalance(int type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Fetch the current balance
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USER + " LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            double currentBalance = cursor.getDouble(0);
            cursor.close();

            // Update balance: subtract for expense, add for income
            double newBalance = type == 0 ? currentBalance - amount : currentBalance + amount;

            // Update the balance in the database
            ContentValues balanceUpdate = new ContentValues();
            balanceUpdate.put(COLUMN_BALANCE, newBalance);
            db.update(TABLE_USER, balanceUpdate, null, null);
        }

        db.close();
    }

    // Fetch user data
    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM User LIMIT 1", null);
    }

    // Get last transactions
    public Cursor getLastTransactions(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " ORDER BY " + COLUMN_DATE + " DESC LIMIT " + limit, null);
    }

    public void deleteExpense(long transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});
        db.close();
    }

    public void deleteRegularExpense(long transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete from RegularTransaction table first (because it references Transaction table)
        db.delete(TABLE_REGULAR_TRANSACTION, COLUMN_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});

        // Now delete from Transaction table
        db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transactionId)});

        db.close();
    }

    public void addUser(String name, String email, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("balance", balance);

        db.insert("User", null, values); // Insert into User table
        db.close();
    }
}
