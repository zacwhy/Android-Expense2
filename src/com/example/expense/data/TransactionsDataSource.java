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
	
	private static final String QUOTED_TABLE_NAME = SqlHelper.quote(ExpenseContract.Transaction.TABLE_NAME);

	private SQLiteDatabase mDatabase;
	
	public TransactionsDataSource(SQLiteDatabase database) {
		mDatabase = database;
	}
	
	public int insert(List<Transaction> transactions) {
	    int rowsAffected = 0;
	    
        for (Transaction transaction : transactions) {
            insert(transaction);
            rowsAffected++;
        }
        
        return rowsAffected;
    }
	
	private long insert(Transaction transaction) {
		ContentValues values = new ContentValues();
		
		values.put(ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID, transaction.getTransactionGroup().getId());
		values.put(ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE, transaction.getSequence());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID, transaction.getToAccount().getId());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_AMOUNT, transaction.getAmount().toPlainString());
        values.put(ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION, transaction.getDescription());

		long newRowId = mDatabase.insert(QUOTED_TABLE_NAME, null, values);
		return newRowId;
	}
	
	public int insertOrUpdate(List<Transaction> transactions) {
	    int rowsAffected = 0;
	    
	    for (Transaction transaction : transactions) {
	        rowsAffected += insertOrUpdate(transaction);
        }
	    
	    return rowsAffected;
	}
	
	private int insertOrUpdate(Transaction transaction) {
	    Transaction oldValue = getById(transaction.getId());
	    return insertOrUpdate(transaction, oldValue);
	}
	
	private int insertOrUpdate(Transaction newValue, Transaction oldValue) {
	    int rowsAffected;
	    
	    if (oldValue == null) {
            insert(newValue);
            rowsAffected = 1;
        } else {
            rowsAffected = update(newValue, oldValue);
        }
	    
	    return rowsAffected;
	}
	
	public int update(Transaction newValue, Transaction oldValue) {
        int rowsAffected = 0;
        
        ContentValues values = getUpdateContentValues(newValue, oldValue);
        
        if (values.size() > 0) {
            rowsAffected = updateById(newValue.getId(), values);
        }
        
        return rowsAffected;
	}
	
	private ContentValues getUpdateContentValues(Transaction newValue, Transaction oldValue) {
        ContentValues values = new ContentValues();

        if (newValue.getToAccount().getId() != oldValue.getToAccount().getId()) {
            values.put(ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID, newValue.getToAccount().getId());
        }
        
        if (newValue.getAmount().compareTo(oldValue.getAmount()) != 0) {
            values.put(ExpenseContract.Transaction.COLUMN_NAME_AMOUNT, newValue.getAmount().toPlainString());
        }
        
        if (!newValue.getDescription().contentEquals(oldValue.getDescription())) {
            values.put(ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION, newValue.getDescription());
        }
        
        return values;
    }
	
	public int updateById(long id, ContentValues values) {
        String whereClause = ExpenseContract.Transaction._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(id) };
        return update(values, whereClause, whereArgs);
    }
    
    private int update(ContentValues values, String whereClause, String[] whereArgs) {
        int rowsAffected = mDatabase.update(QUOTED_TABLE_NAME, values, whereClause, whereArgs);
        return rowsAffected;
    }
    
    // TODO
    public int deleteByIds(List<Long> ids) {
        int result = 0;
        
        for (Long id : ids) {
            result += deleteById(id);
        }
        
        return result;
    }
	
	public int deleteById(long id) {
	    String whereClause = ExpenseContract.Transaction._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(id) };
        return delete(whereClause, whereArgs);
	}
	
	public int deleteByTransactionGroupId(long transactionGroupId) {
        String whereClause = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(transactionGroupId) };
        return delete(whereClause, whereArgs);
	}
	
	private int delete(String whereClause, String[] whereArgs) {
	    int rowsAffected = mDatabase.delete(QUOTED_TABLE_NAME, whereClause, whereArgs);
        return rowsAffected;
	}
	
	public Transaction getById(long id) {
	    String selection = ExpenseContract.Transaction._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        List<Transaction> transactions = getList(selection, selectionArgs);
        
        if (transactions.size() == 0) {
            return null;
        }
        
        return transactions.get(0);
	}
	
	public List<Transaction> getListByTransactionGroupId(long transactionGroupId) {
        String selection = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " = ?";
        String[] selectionArgs = { Long.toString(transactionGroupId) };
        return getList(selection, selectionArgs);
	}
    
    private List<Transaction> getList(String selection, String[] selectionArgs) {
        Cursor cursor = getCursor(selection, selectionArgs);
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
    
    public Cursor getCursor(String selection, String[] selectionArgs) {
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
	    Cursor cursor = mDatabase.query(QUOTED_TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
	    return cursor;
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
