package com.zacwhy.expense.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.expense.data.ExpenseDatabaseHelper;
import com.example.expense.data.TransactionGroupsDataSource;

public class SummaryProvider extends ContentProvider {

    public static final String AUTHORITY = "com.zacwhy.expense.provider.summaries";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + Summaries.CONTENT_PATH);

    private static final int GROUP_BY_CATEGORY = 1;

    private static final UriMatcher sUriMatcher;

    public static interface Summaries {
        //public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.expense.accounts";
        public static final String CONTENT_PATH = "summaries";
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, Summaries.CONTENT_PATH, GROUP_BY_CATEGORY);
    }

    private SQLiteDatabase mDb = null;

    @Override
    public boolean onCreate() {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(getContext());
        mDb = databaseHelper.getReadableDatabase();

        if (mDb == null) {
            return false;
        }

        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
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
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
        case GROUP_BY_CATEGORY:
            cursor = TransactionGroupsDataSource.queryGroupByCategory(mDb, selection, selectionArgs, sortOrder);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

}
