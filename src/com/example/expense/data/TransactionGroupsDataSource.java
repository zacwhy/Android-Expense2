package com.example.expense.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.SqlHelper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupsDataSource {
	
	private static final String QUOTED_TABLE_NAME = 
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
        values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID, 
                transactionGroup.getFromAccount().getId());

		long newRowId = database.insert(QUOTED_TABLE_NAME, null, values);
		return newRowId;
	}
	
	public int updateById(long id, ContentValues values) {
	    String whereClause = ExpenseContract.TransactionGroup._ID + " = ?";
	    String[] whereArgs = new String[] { Long.toString(id) };
	    return update(values, whereClause, whereArgs);
	}
	
	private int update(ContentValues values, String whereClause, String[] whereArgs) {
	    int rowsAffected = database.update(QUOTED_TABLE_NAME, values, whereClause, whereArgs);
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
	    int rowsAffected = database.delete(QUOTED_TABLE_NAME, whereClause, whereArgs);
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
	            ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID,
	            ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID
	    };
	    
	    String groupBy = null;
	    String having = null;
	    String orderBy = null;

	    Cursor cursor = database.query(QUOTED_TABLE_NAME, columns, selection, selectionArgs, groupBy, 
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
	    ExpenseCategory expenseCategory = new ExpenseCategory(cursor.getLong(3));
	    Account fromAccount = new Account(cursor.getLong(4));
	    
	    TransactionGroup transactionGroup = new TransactionGroup();
	    transactionGroup.setId(id);
	    transactionGroup.setDate(date);
	    transactionGroup.setSequence(sequence);
	    transactionGroup.setExpenseCategory(expenseCategory);
	    transactionGroup.setFromAccount(fromAccount);
	    
	    return transactionGroup;
	}
	
	public Map<Long, TransactionGroup> getTransactionGroupsWithTransactions(String selection, 
            String[] selectionArgs) {
        
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        
        String inTables = SqlHelper.format("%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup._ID);
        
        sqliteQueryBuilder.setTables(inTables);
        
        String[] projectionIn = {
                SqlHelper.format("%s.%s", ExpenseContract.TransactionGroup.TABLE_NAME, ExpenseContract.TransactionGroup._ID),
                SqlHelper.format("%s.%s", ExpenseContract.TransactionGroup.TABLE_NAME, ExpenseContract.TransactionGroup.COLUMN_NAME_DATE),
                SqlHelper.format("%s.%s", ExpenseContract.TransactionGroup.TABLE_NAME, ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE),
                SqlHelper.format("%s.%s", ExpenseContract.TransactionGroup.TABLE_NAME, ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID),
                SqlHelper.format("%s.%s", ExpenseContract.TransactionGroup.TABLE_NAME, ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID),
                
                SqlHelper.format("%s.%s", ExpenseContract.Transaction.TABLE_NAME, ExpenseContract.Transaction._ID),
                SqlHelper.format("%s.%s", ExpenseContract.Transaction.TABLE_NAME, ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE),
                SqlHelper.format("%s.%s", ExpenseContract.Transaction.TABLE_NAME, ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID),
                SqlHelper.format("%s.%s", ExpenseContract.Transaction.TABLE_NAME, ExpenseContract.Transaction.COLUMN_NAME_AMOUNT),
                SqlHelper.format("%s.%s", ExpenseContract.Transaction.TABLE_NAME, ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION)
        };
        
        String groupBy = null;
        String having = null;
        String sortOrder = null;
        
        Cursor cursor = sqliteQueryBuilder.query(database, projectionIn, selection, selectionArgs, 
                groupBy, having, sortOrder);
        
        Map<Long, TransactionGroup> transactionGroupsMap = new HashMap<Long, TransactionGroup>();
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            TransactionGroup transactionGroup;
            long transactionGroupId = cursor.getLong(0);
            if (transactionGroupsMap.containsKey(transactionGroupId)) {
                transactionGroup = transactionGroupsMap.get(transactionGroupId);
            } else {
                transactionGroup = cursorToTransactionGroup2(cursor, transactionGroupId);
                transactionGroupsMap.put(transactionGroupId, transactionGroup);
            }
            
            Transaction transaction = cursorToTransaction(cursor);
            transactionGroup.getTransactions().add(transaction);
            
            cursor.moveToNext();
        }
        
        // Make sure to close the cursor
        cursor.close();
        
        return transactionGroupsMap;
    }
	
	private Transaction cursorToTransaction(Cursor cursor) {
	    long transactionId = cursor.getLong(5);
	    int sequence = cursor.getInt(6);
	    Account toAccount = new Account(cursor.getLong(7));
        BigDecimal amount = new BigDecimal(cursor.getString(8));
        String description = cursor.getString(9);
        
        Transaction transaction = new Transaction(transactionId);
        transaction.setSequence(sequence);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        
        return transaction;
	}
	
	private TransactionGroup cursorToTransactionGroup2(Cursor cursor, long id) {
	    Calendar date = DateHelper.getCalendarFromMilliseconds(cursor.getLong(1));
        int sequence = cursor.getInt(2);
        ExpenseCategory expenseCategory = new ExpenseCategory(cursor.getLong(3));
        Account fromAccount = new Account(cursor.getLong(4));
        
        TransactionGroup transactionGroup = new TransactionGroup(id);
        transactionGroup.setDate(date);
        transactionGroup.setSequence(sequence);
        transactionGroup.setExpenseCategory(expenseCategory);
        transactionGroup.setFromAccount(fromAccount);
        
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
