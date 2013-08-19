package com.example.expense.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;

public class AccountsDataSource {
	
	private static final String TABLE_NAME = ExpenseContract.Account.TABLE_NAME;

	private SQLiteDatabase database;
	
	public AccountsDataSource(SQLiteDatabase database) {
		this.database = database;
	}
	
	public long insert(String name) {
		ContentValues values = new ContentValues();
		values.put(ExpenseContract.Account.COLUMN_NAME_NAME, name);

		long newRowId = database.insert(TABLE_NAME, null, values);
		return newRowId;
	}
	
	public int update(long rowId, String name) {
		ContentValues values = new ContentValues();
		values.put(ExpenseContract.Account.COLUMN_NAME_NAME, name);

		String selection = ExpenseContract.Account._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(rowId) };

		int count = database.update(TABLE_NAME, values, selection, selectionArgs);
		return count;
	}
	
	public int delete(Account item) {
		long id = item.getId();
		int count = database.delete(TABLE_NAME, ExpenseContract.Account._ID + " = " + id, null);
		return count;
	}
	
	public int deleteAll() {
		int count = database.delete(TABLE_NAME, "1", null);
		return count;
	}
	
	public List<Account> getAll() {
		List<Account> items = new ArrayList<Account>();

		String[] allColumns = { 
				ExpenseContract.Account._ID,
				ExpenseContract.Account.COLUMN_NAME_NAME
		};
		
		Cursor cursor = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
		    Account item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return items;
	}
	
	private Account cursorToItem(Cursor cursor) {
	    Account item = new Account();
		item.setId(cursor.getLong(0));
		item.setName(cursor.getString(1));
		return item;
	}

}
