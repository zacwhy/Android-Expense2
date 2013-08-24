package com.example.expense.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.SqlHelper;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
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
	
	public TransactionGroup getTransactionGroupWithTransactionsById(long id) {
	    TransactionGroup transactionGroup = getTransactionGroupById(id);
	    
	    TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
	    List<Transaction> transactions = 
	            transactionsDataSource.getTransactionsByTransactionGroupId(id);
	    
	    transactionGroup.setTransactions(transactions);
	    
	    return transactionGroup;
	}
	
	public TransactionGroup getTransactionGroupById(long id) {
        String selection = ExpenseContract.TransactionGroup._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        
        List<TransactionGroup> items = get(selection, selectionArgs);
        
        if (items.size() == 0) {
            return null;
        }
        
        return items.get(0);
	}
	
	private List<TransactionGroup> get(String selection, String[] selectionArgs) {
	    String[] columns = {
	            ExpenseContract.TransactionGroup._ID,
	            ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
	            ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE,
	            ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID
	    };
	    
	    String groupBy = null;
	    String having = null;
	    String orderBy = null;

	    Cursor cursor = database.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, 
	            having, orderBy);
	    
	    List<TransactionGroup> items = new ArrayList<TransactionGroup>();
	    
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	        TransactionGroup item = cursorToTransactionGroup(cursor);
	        items.add(item);
	        cursor.moveToNext();
	    }
	    
	    // Make sure to close the cursor
	    cursor.close();
	    return items;
	}
	
	private TransactionGroup cursorToTransactionGroup(Cursor cursor) {
	    long id = cursor.getLong(0);
	    Calendar date = DateHelper.getCalendarFromMilliseconds(cursor.getLong(1));
	    int sequence = cursor.getInt(2);
	    long expenseCategoryId = cursor.getLong(3);
	    
	    ExpenseCategory expenseCategory = new ExpenseCategory();
	    expenseCategory.setId(expenseCategoryId);
	    
	    TransactionGroup transactionGroup = new TransactionGroup();
	    transactionGroup.setId(id);
	    transactionGroup.setDate(date);
	    transactionGroup.setSequence(sequence);
	    transactionGroup.setExpenseCategory(expenseCategory);
	    
	    return transactionGroup;
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
