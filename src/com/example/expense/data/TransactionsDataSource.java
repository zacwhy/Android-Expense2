package com.example.expense.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

public class TransactionsDataSource {
	
	private static final String TABLE_NAME = 
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
	    int rowsAffected = database.delete(TABLE_NAME, whereClause, whereArgs);
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
                ExpenseContract.Transaction.COLUMN_NAME_FROM_ACCOUNT_ID,
                ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID,
                ExpenseContract.Transaction.COLUMN_NAME_AMOUNT,
	            ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION
	    };
	    
	    String groupBy = null;
	    String having = null;
	    String orderBy = null;
	    
	    Cursor cursor = database.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, 
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
	    long id = cursor.getLong(0);
        long transactionGroupId = cursor.getLong(1);
        int sequence = cursor.getInt(2);
        long fromAccountId = cursor.getLong(3);
        long toAccountId = cursor.getLong(4);
        BigDecimal amount = new BigDecimal(cursor.getString(5));
        String description = cursor.getString(6);
        
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setId(transactionGroupId);
        
        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        
        Account toAccount = new Account();
        toAccount.setId(toAccountId);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setTransactionGroup(transactionGroup);
        transaction.setSequence(sequence);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transaction;
	}
	
	public List<Transaction> getTransactions(Map<Long, Account> accounts, 
            Map<Long, ExpenseCategory> expenseCategories) {
	    
        return getTransactionsWithParameters(null, null, accounts, expenseCategories);
    }
    
    private List<Transaction> getTransactionsWithParameters(String selection, 
            String[] selectionArgs,
            Map<Long, Account> accounts, 
            Map<Long, ExpenseCategory> expenseCategories) {
        
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        
        String inTables = SqlHelper.format("%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup._ID);
        
        sqliteQueryBuilder.setTables(inTables);
        
        String[] projectionIn = null;
        String groupBy = null;
        String having = null;
        
        String sortOrder = SqlHelper.format("%s.%s DESC, %s.%s DESC",
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE);
        
        Cursor cursor = sqliteQueryBuilder.query(database, projectionIn, selection, selectionArgs, 
                groupBy, having, sortOrder);
        
        List<Transaction> items = new ArrayList<Transaction>();
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction item = cursorToTransaction(cursor, accounts, expenseCategories);
            items.add(item);
            cursor.moveToNext();
        }
        
        // Make sure to close the cursor
        cursor.close();
        
        return items;
    }
    
    private Transaction cursorToTransaction(Cursor cursor, Map<Long, Account> accounts, 
            Map<Long, ExpenseCategory> expenseCategories) {
        
        long transactionId = cursor.getLong(0);
        long transactionGroupId = cursor.getLong(1);
        int transactionSequence = cursor.getInt(2);
        long fromAccountId = cursor.getLong(3);
        long toAccountId = cursor.getLong(4);
        BigDecimal amount = new BigDecimal(cursor.getString(5));
        String description = cursor.getString(6);
        //long transactionGroupId2 = cursor.getLong(7);
        Calendar date = DateHelper.getCalendarFromMilliseconds(cursor.getLong(8));
        int transactionGroupSequence = cursor.getInt(9);
        long expenseCategoryId = cursor.getLong(10);
        
        ExpenseCategory expenseCategory = expenseCategories.get(expenseCategoryId);
        Account fromAccount = accounts.get(fromAccountId);
        Account toAccount = accounts.get(toAccountId);

        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setId(transactionGroupId);
        transactionGroup.setDate(date);
        transactionGroup.setSequence(transactionGroupSequence);
        transactionGroup.setExpenseCategory(expenseCategory);
        
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setTransactionGroup(transactionGroup);
        transaction.setSequence(transactionSequence);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transaction;
    }

}
