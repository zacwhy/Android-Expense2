package com.example.expense.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Transaction;

public class TransactionsDataSource {
	
	private static final String TABLE_NAME = "\"" + ExpenseContract.Transaction.TABLE_NAME + "\"";

	private SQLiteDatabase database;
	
	public TransactionsDataSource(SQLiteDatabase database) {
		this.database = database;
	}
	
	public long insert(Transaction transaction) {
		ContentValues values = new ContentValues();
		
		values.put(ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID, 
		        transaction.getTransactionGroup().getId());
		values.put(ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE, 
		        transaction.getSequence());
		values.put(ExpenseContract.Transaction.COLUMN_NAME_FROM_ACCOUNT_ID, 
		        transaction.getFromAccount().getId());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID, 
                transaction.getToAccount().getId());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_AMOUNT, 
                transaction.getAmount().toPlainString());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION, 
                transaction.getDescription());

		long newRowId = database.insert(TABLE_NAME, null, values);
		return newRowId;
	}

}
