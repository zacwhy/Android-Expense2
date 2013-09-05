package com.example.expense.content;

import com.example.expense.data.AccountsDataSource;
import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionsDataSource;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AccountProvider extends ContentProvider {
    
    public final static String AUTHORITY = "com.zacwhy.expense.provider";
    public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/accounts");
    public final static Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY  + "/transactions");

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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(
            Uri uri, 
            String[] projection, 
            String selection, 
            String[] selectionArgs, 
            String sortOrder) {
        
        int code = sUriMatcher.match(uri);
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        switch (code) {
        
        case 1:
            AccountsDataSource accountsDataSource = new AccountsDataSource(db);
            Cursor cursor = accountsDataSource.getCursor();
            return cursor;
            
        case 2:
            TransactionsDataSource transactionsDataSource = new TransactionsDataSource(db);
            Cursor cursor2 = transactionsDataSource.getCursor(selection, selectionArgs);
            return cursor2;
            
        }
        
        throw new IllegalArgumentException();
        
        //readableDatabase.close();
        
        //return cursor;
    }
    
    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher;
    
    static {
        sUriMatcher = new UriMatcher(0);
        sUriMatcher.addURI(AUTHORITY, "accounts", 1);
        sUriMatcher.addURI(AUTHORITY, "transactions", 2);
    }

}
