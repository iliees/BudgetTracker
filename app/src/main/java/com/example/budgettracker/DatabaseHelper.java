package com.example.budgettracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "BudgetTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table User
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USER_ID = "User_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_BALANCE = "balance";

    // Table Transaction (escape the name because "Transaction" is a reserved keyword)
    private static final String TABLE_TRANSACTION = "`Transaction`"; // Use backticks to escape
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_TYPE = "type"; // Bool (0 for expense, 1 for income)
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "transaction_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location"; // New attribute for location

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
                + COLUMN_TYPE + " INTEGER, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT" // Add location column here
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION); // Escaped name works here too
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGULAR_TRANSACTION);
        onCreate(db);
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
