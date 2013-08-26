package com.example.expense.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.helpers.SqlHelper;
import com.example.expense.models.Account;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class TransactionsDataSource {
	
	private static final String QUOTED_TABLE_NAME = 
	        SqlHelper.quote(ExpenseContract.Transaction.TABLE_NAME);

	private SQLiteDatabase database;
	
	public TransactionsDataSource(SQLiteDatabase database) {
		this.database = database;
	}
	
	public void insert(List<Transaction> transactions, long transactionGroupId) {
        for (Transaction transaction : transactions) {
            simpleInsert(transaction, transactionGroupId);
        }
    }
	
	private long simpleInsert(Transaction transaction, long transactionGroupId) {
		ContentValues values = new ContentValues();
		
		values.put(ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID, 
		        transactionGroupId);
		values.put(ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE, transaction.getSequence());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID, 
                transaction.getToAccount().getId());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_AMOUNT, 
                transaction.getAmount().toPlainString());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION, 
                transaction.getDescription());

		long newRowId = database.insert(QUOTED_TABLE_NAME, null, values);
		return newRowId;
	}
	
	public int updateById(long id, ContentValues values) {
        String whereClause = ExpenseContract.Transaction._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(id) };
        return update(values, whereClause, whereArgs);
    }
    
    private int update(ContentValues values, String whereClause, String[] whereArgs) {
        int rowsAffected = database.update(QUOTED_TABLE_NAME, values, whereClause, whereArgs);
        return rowsAffected;
    }
	
	public int deleteByTransaction(Transaction transaction) {
	    return deleteByTransactionId(transaction.getId());
	}
	
	public int deleteByTransactionId(long transactionId) {
	    String whereClause = ExpenseContract.Transaction._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(transactionId) };
        return delete(whereClause, whereArgs);
	}
	
	public int deleteByTransactionGroupId(long transactionGroupId) {
        String whereClause = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(transactionGroupId) };
        return delete(whereClause, whereArgs);
	}
	
	private int delete(String whereClause, String[] whereArgs) {
	    int rowsAffected = database.delete(QUOTED_TABLE_NAME, whereClause, whereArgs);
        return rowsAffected;
	}
	
	public List<Transaction> getTransactionsByTransactionGroupId(long transactionGroupId) {
        String selection = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " = ?";
        String[] selectionArgs = { Long.toString(transactionGroupId) };
        
        return get(selection, selectionArgs);    
	}
	
	private List<Transaction> get(String selection, String[] selectionArgs) {
	    
	    String[] columns = {
	            ExpenseContract.Transaction._ID,
                ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID,
	            ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE,
                ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID,
                ExpenseContract.Transaction.COLUMN_NAME_AMOUNT,
	            ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION
	    };
	    
	    String groupBy = null;
	    String having = null;
	    String orderBy = null;
	    
	    Cursor cursor = database.query(QUOTED_TABLE_NAME, columns, selection, selectionArgs, groupBy, 
	            having, orderBy);
	    
	    List<Transaction> items = new ArrayList<Transaction>();

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	        Transaction item = cursorToTransaction(cursor);
	        items.add(item);
	        cursor.moveToNext();
	    }

	    // Make sure to close the cursor
	    cursor.close();
	    return items;
	}
	
	private Transaction cursorToTransaction(Cursor cursor) {
        TransactionGroup transactionGroup = new TransactionGroup(cursor.getLong(1));
        int sequence = cursor.getInt(2);
        Account toAccount = new Account(cursor.getLong(3));
        BigDecimal amount = new BigDecimal(cursor.getString(4));
        String description = cursor.getString(5);

        Transaction transaction = new Transaction(cursor.getLong(0));
        transaction.setTransactionGroup(transactionGroup);
        transaction.setSequence(sequence);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transaction;
	}

}
