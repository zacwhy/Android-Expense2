package com.example.expense.data;

import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.helpers.DateHelper;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupsDataSource {
	
	private static final String TABLE_NAME = ExpenseContract.TransactionGroup.TABLE_NAME;

	private SQLiteDatabase database;
	
	public TransactionGroupsDataSource(SQLiteDatabase database) {
		this.database = database;
	}
	
	public long insert(TransactionGroup transactionGroup) {
		ContentValues values = new ContentValues();
		
		values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_DATE, 
		        transactionGroup.getDate().getTimeInMillis());
		values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE, 
		        transactionGroup.getSequence());
		values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID, 
		        transactionGroup.getExpenseCategory().getId());

		long newRowId = database.insert(TABLE_NAME, null, values);
		return newRowId;
	}
	
	//@SuppressLint("SimpleDateFormat")
    public int getMaxSequence(Calendar date) {
	    int sequence = 0;
	    
	    String dateString = DateHelper.getSqlDateString(date);
	    
        String sql = "SELECT MAX(\"sequence\") FROM \"transaction_group\"" + 
                " WHERE datetime(\"date\"/1000, 'unixepoch') >= ?";

	    Cursor cursor = database.rawQuery(sql, new String[] { dateString }); 
	    
	    cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sequence = cursor.getInt(0);
            cursor.moveToNext();
        }
        
        // Make sure to close the cursor
        cursor.close();
	    
	    return sequence;
	}

}
