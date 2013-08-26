package com.example.expense.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.ExpenseCategory;

public class ExpenseCategoriesDataSource {

	private SQLiteDatabase database;
	//private ExpenseDbHelper dbHelper;
	
	private String[] allColumns = { 
			ExpenseContract.ExpenseCategory._ID,
			ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE
	};
	
	public ExpenseCategoriesDataSource(SQLiteDatabase database) {
		this.database = database;
	}

//	public ExpenseCategoriesDataSource(Context context) {
//		dbHelper = new ExpenseDbHelper(context);
//	}
//
//	public void open() throws SQLException {
//		database = dbHelper.getWritableDatabase();
//	}
//
//	public void close() {
//		dbHelper.close();
//	}
	
	public long createExpenseCategory(String title) {
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE, title);
		
		// Insert the new row, returning the primary key value of the new row
		long newRowId = database.insert(
				ExpenseContract.ExpenseCategory.TABLE_NAME,
				null,
				values);
		
//		Cursor cursor = database.query(ExpenseContract.ExpenseCategory.TABLE_NAME, allColumns, 
//				ExpenseContract.ExpenseCategory._ID + " = " + insertId, null, null, null, null);
//		cursor.moveToFirst();
//		ExpenseCategory expenseCategory = cursorToExpenseCategory(cursor);
//		cursor.close();
		
		return newRowId;
	}
	
	public int updateExpenseCategory(long rowId, String title) {
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE, title);

		// Which row to update, based on the ID
		String selection = ExpenseContract.ExpenseCategory._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(rowId) };

		int count = database.update(
				ExpenseContract.ExpenseCategory.TABLE_NAME,
				values,
				selection,
				selectionArgs);

		return count;
	}
	
	public int deleteAll() {
		int count = database.delete(ExpenseContract.ExpenseCategory.TABLE_NAME, "1", null);
		return count;
	}

	public List<ExpenseCategory> getAll() {
		List<ExpenseCategory> expenseCategories = new ArrayList<ExpenseCategory>();

		Cursor cursor = database.query(ExpenseContract.ExpenseCategory.TABLE_NAME, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ExpenseCategory expenseCategory = cursorToExpenseCategory(cursor);
			expenseCategories.add(expenseCategory);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return expenseCategories;
	}
	
	private ExpenseCategory cursorToExpenseCategory(Cursor cursor) {
		ExpenseCategory expenseCategory = new ExpenseCategory(cursor.getLong(0));
		expenseCategory.setTitle(cursor.getString(1));
		return expenseCategory;
	}

}
