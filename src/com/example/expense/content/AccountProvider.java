package com.example.expense.content;

import com.example.expense.data.AccountsDataSource;
import com.example.expense.data.ExpenseDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AccountProvider extends ContentProvider {
    
    public final static String AUTHORITY = "com.zacwhy.expense.provider";
    public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/accounts");

    private ExpenseDatabaseHelper mOpenHelper;
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new ExpenseDatabaseHelper(getContext());

        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, 
            String sortOrder) {
        
        SQLiteDatabase readableDatabase = mOpenHelper.getReadableDatabase();
        
        AccountsDataSource accountsDataSource = new AccountsDataSource(readableDatabase);
        
        Cursor cursor = accountsDataSource.getCursor();
        
        //readableDatabase.close();
        
        return cursor;
    }

}
