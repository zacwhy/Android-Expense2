package com.example.expense.data;

import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.SqlHelper;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupsDataSource {
	
	private static final String TABLE_NAME = 
	        SqlHelper.quote(ExpenseContract.TransactionGroup.TABLE_NAME);

	private SQLiteDatabase database;
	
	public TransactionGroupsDataSource(SQLiteDatabase database) {
		this.database = database;
	}
	
	public long insertLast(TransactionGroup transactionGroup) {
        int sequence = getMaxSequence(transactionGroup.getDate()) + 1;
        transactionGroup.setSequence(sequence);
        
        long transactionGroupId = simpleInsert(transactionGroup);
        
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
        transactionsDataSource.insert(transactionGroup.getTransactions(), transactionGroupId);
        
        return transactionGroupId;
	}
	
	private long simpleInsert(TransactionGroup transactionGroup) {
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
	
	public int updateById(long id, ContentValues values) {
	    String whereClause = ExpenseContract.TransactionGroup._ID + " = ?";
	    String[] whereArgs = new String[] { Long.toString(id) };
	    return update(values, whereClause, whereArgs);
	}
	
	private int update(ContentValues values, String whereClause, String[] whereArgs) {
	    int rowsAffected = database.update(TABLE_NAME, values, whereClause, whereArgs);
	    return rowsAffected;
	}
	
	public int deleteByTransactionGroup(TransactionGroup transactionGroup) {
        return deleteByTransactionGroupId(transactionGroup.getId());
	}
	
	public int deleteByTransactionGroupId(long transactionGroupId) {
	    // Delete child records first
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
        transactionsDataSource.deleteByTransactionGroupId(transactionGroupId);
        
        String whereClause = ExpenseContract.TransactionGroup._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(transactionGroupId) };
        return delete(whereClause, whereArgs);
    }
	
	private int delete(String whereClause, String[] whereArgs) {
	    int rowsAffected = database.delete(TABLE_NAME, whereClause, whereArgs);
        return rowsAffected;
	}
	
	private int getMaxSequence(Calendar calendar) {
	    int sequence = 0;

	    String sql = SqlHelper.format("SELECT MAX(%s) FROM %s" +
	            " WHERE datetime(%s/1000, 'unixepoch') BETWEEN ? AND datetime(?, '+1 day')",
	            ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE,
	            ExpenseContract.TransactionGroup.TABLE_NAME,
	            ExpenseContract.TransactionGroup.COLUMN_NAME_DATE);
        
        Calendar universalTimeCalendar = DateHelper.getUniversalTime(calendar);
        String dateTimeString = DateHelper.getSqlDateTimeString(universalTimeCalendar);

	    Cursor cursor = database.rawQuery(sql, new String[] { dateTimeString, dateTimeString }); 
	    
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
