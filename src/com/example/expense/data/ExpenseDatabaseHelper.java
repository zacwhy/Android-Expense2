package com.example.expense.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expense.data.ExpenseContract.Account;
import com.example.expense.data.ExpenseContract.ExpenseCategory;
import com.example.expense.data.ExpenseContract.Transaction;
import com.example.expense.data.ExpenseContract.TransactionGroup;

public class ExpenseDatabaseHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Expense.db";

	public ExpenseDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    db.execSQL(SQL_CREATE_ACCOUNTS);
		db.execSQL(SQL_CREATE_EXPENSE_CATEGORIES);
		db.execSQL(SQL_CREATE_TRANSACTION_GROUPS);
        db.execSQL(SQL_CREATE_TRANSACTIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    if (oldVersion < 11) {
	        // upgrade from 10 to 11
	    }
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY";
    private static final String COMMA_SEP = ",";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String TEXT_TYPE = " TEXT";
	
    private static final String SQL_CREATE_ACCOUNTS =
            CREATE_TABLE + Account.TABLE_NAME + " (" +
                    Account._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    Account.COLUMN_NAME_NAME + TEXT_TYPE +
                    ")";
    
	private static final String SQL_CREATE_EXPENSE_CATEGORIES =
	        CREATE_TABLE + ExpenseCategory.TABLE_NAME + " (" +
					ExpenseCategory._ID + INTEGER_PRIMARY_KEY + 
					COMMA_SEP + ExpenseCategory.COLUMN_NAME_TITLE + TEXT_TYPE +
					")";
    
    private static final String SQL_CREATE_TRANSACTION_GROUPS =
            CREATE_TABLE + TransactionGroup.TABLE_NAME + " (" +
                    TransactionGroup._ID + INTEGER_PRIMARY_KEY + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_DATE + INTEGER_TYPE + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_SEQUENCE + INTEGER_TYPE + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID + INTEGER_TYPE + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID + INTEGER_TYPE + 
                    ")";
    
    private static final String SQL_CREATE_TRANSACTIONS =
            CREATE_TABLE + "\"" + Transaction.TABLE_NAME + "\" (" +
                    Transaction._ID + INTEGER_PRIMARY_KEY + 
                    COMMA_SEP + Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + INTEGER_TYPE + 
                    COMMA_SEP + Transaction.COLUMN_NAME_SEQUENCE + INTEGER_TYPE + 
                    COMMA_SEP + Transaction.COLUMN_NAME_TO_ACCOUNT_ID + INTEGER_TYPE + 
                    COMMA_SEP + Transaction.COLUMN_NAME_AMOUNT + REAL_TYPE + 
                    COMMA_SEP + Transaction.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + 
                    ")";

}
