package com.example.expense.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expense.data.ExpenseContract.Account;
import com.example.expense.data.ExpenseContract.ExpenseCategory;
import com.example.expense.data.ExpenseContract.Transaction;
import com.example.expense.data.ExpenseContract.TransactionGroup;

public class ExpenseDbHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Expense.db";

	public ExpenseDbHelper(Context context) {
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
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
	    
//	    db.execSQL(SQL_DELETE_ACCOUNTS);
//		db.execSQL(SQL_DELETE_EXPENSE_CATEGORIES);
//		db.execSQL(SQL_DELETE_TRANSACTION_GROUPS);
//        db.execSQL(SQL_DELETE_TRANSACTIONS);

	    //		onCreate(db);
	    
	    
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	//private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
	
    private static final String SQL_CREATE_ACCOUNTS =
            "CREATE TABLE " + Account.TABLE_NAME + " (" +
                    Account._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    Account.COLUMN_NAME_NAME + TEXT_TYPE +
                    ")";

//    private static final String SQL_DELETE_ACCOUNTS =
//            "DROP TABLE IF EXISTS " + Account.TABLE_NAME;
    
	private static final String SQL_CREATE_EXPENSE_CATEGORIES =
			"CREATE TABLE " + ExpenseCategory.TABLE_NAME + " (" +
					ExpenseCategory._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
					ExpenseCategory.COLUMN_NAME_TITLE + TEXT_TYPE +
					")";

//	private static final String SQL_DELETE_EXPENSE_CATEGORIES =
//			"DROP TABLE IF EXISTS " + ExpenseCategory.TABLE_NAME;
    
    private static final String SQL_CREATE_TRANSACTION_GROUPS =
            "CREATE TABLE " + TransactionGroup.TABLE_NAME + " (" +
                    TransactionGroup._ID + " INTEGER PRIMARY KEY" + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_DATE + " INTEGER" + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_SEQUENCE + " INTEGER" + 
                    COMMA_SEP + TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID + " INTEGER" + 
                    ")";

//    private static final String SQL_DELETE_TRANSACTION_GROUPS = 
//            DROP_TABLE_IF_EXISTS + TransactionGroup.TABLE_NAME;
    
    private static final String SQL_CREATE_TRANSACTIONS =
            "CREATE TABLE \"" + Transaction.TABLE_NAME + "\" (" +
                    Transaction._ID + " INTEGER PRIMARY KEY" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " INTEGER" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_SEQUENCE + " INTEGER" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_FROM_ACCOUNT_ID + " INTEGER" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_TO_ACCOUNT_ID + " INTEGER" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_AMOUNT + " REAL" + 
                    COMMA_SEP + Transaction.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + 
                    ")";

//    private static final String SQL_DELETE_TRANSACTIONS = 
//            DROP_TABLE_IF_EXISTS + "\"" + Transaction.TABLE_NAME + "\"";

}
