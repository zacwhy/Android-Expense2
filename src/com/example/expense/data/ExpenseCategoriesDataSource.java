package com.example.expense.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.ExpenseCategory;

public class ExpenseCategoriesDataSource {

    private SQLiteDatabase mDb;

    private static final String[] sAllColumns = { 
        ExpenseContract.ExpenseCategory._ID,
        ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE
    };

    public ExpenseCategoriesDataSource(SQLiteDatabase db) {
        mDb = db;
    }

    public long insert(String name) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE, name);
        return mDb.insert(ExpenseContract.ExpenseCategory.TABLE_NAME, null, values);
    }

    public int update(long id, String name) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE, name);
        String whereClause = ExpenseContract.ExpenseCategory._ID + " = ?";
        String[] whereArgs = { String.valueOf(id) };
        return mDb.update(ExpenseContract.ExpenseCategory.TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteAll() {
        String whereClause = "1";
        String[] whereArgs = null;
        return delete(whereClause, whereArgs);
    }

    private int delete(String whereClause, String[] whereArgs) {
        return mDb.delete(ExpenseContract.ExpenseCategory.TABLE_NAME, whereClause, whereArgs);
    }

    public ExpenseCategory getById(long id) {
        String selection = ExpenseContract.ExpenseCategory._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        List<ExpenseCategory> list = getList(selection, selectionArgs);

        if (list.size() <= 0) {
            return null;
        }

        return list.get(0);
    }

    public List<ExpenseCategory> getList() {
        return getList(null, null);
    }

    public List<ExpenseCategory> getList(String selection, String[] selectionArgs) {
        List<ExpenseCategory> expenseCategories = new ArrayList<ExpenseCategory>();
        Cursor cursor = getCursor(selection, selectionArgs);
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

    private Cursor getCursor(String selection, String[] selectionArgs) {
        return mDb.query(ExpenseContract.ExpenseCategory.TABLE_NAME, sAllColumns, selection, selectionArgs, null, null, null);
    }

    private ExpenseCategory cursorToExpenseCategory(Cursor cursor) {
        ExpenseCategory expenseCategory = new ExpenseCategory(cursor.getLong(0));
        expenseCategory.setName(cursor.getString(1));
        return expenseCategory;
    }

}
