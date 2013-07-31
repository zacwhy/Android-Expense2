package com.example.expense;

import com.example.expense.ExpenseContract.ExpenseCategory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDbHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Expense.db";

	public ExpenseDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_EXPENSE_CATEGORIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_EXPENSE_CATEGORIES);
		onCreate(db);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	private static final String SQL_CREATE_EXPENSE_CATEGORIES =
			"CREATE TABLE " + ExpenseCategory.TABLE_NAME + " (" +
					ExpenseCategory._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
					//ExpenseCategory.COLUMN_NAME_EXPENSE_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
					ExpenseCategory.COLUMN_NAME_TITLE + TEXT_TYPE +
					")";

	private static final String SQL_DELETE_EXPENSE_CATEGORIES =
			"DROP TABLE IF EXISTS " + ExpenseCategory.TABLE_NAME;

}
