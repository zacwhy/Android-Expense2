package com.example.expense.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.example.expense.data.ExpenseContract;
import com.example.expense.data.ExpenseDatabaseHelper;

public class AccountProvider extends ContentProvider {

    public static final String AUTHORITY = "com.zacwhy.expense.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + Accounts.CONTENT_PATH);

    // helper constants for use with the UriMatcher
    private static final int ACCOUNT_LIST = 1;
    private static final int ACCOUNT_ID = 2;

    private static final UriMatcher URI_MATCHER;

    public static interface Accounts extends BaseColumns {
        public static final Uri CONTENT_URI = AccountProvider.CONTENT_URI;
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.expense.accounts";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.expense.accounts";
        public static final String CONTENT_PATH = "accounts";
    }

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, Accounts.CONTENT_PATH, ACCOUNT_LIST);
        URI_MATCHER.addURI(AUTHORITY, Accounts.CONTENT_PATH + "/#", ACCOUNT_ID);
    }

    private SQLiteDatabase mDb = null;

    @Override
    public boolean onCreate() {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(getContext());
        mDb = databaseHelper.getWritableDatabase();

        if (mDb == null) {
            return false;
        }

        if (mDb.isReadOnly()) {
            mDb.close();
            mDb = null;
            return false;
        }

        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != ACCOUNT_LIST) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        long id = mDb.insert(ExpenseContract.Account.TABLE_NAME, null, values);

        if (id > 0) {
            // notify all listeners of changes and return itemUri:
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }

        throw new SQLException("Problem while inserting");
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        int updateCount = 0;

        switch (URI_MATCHER.match(uri)) {

        case ACCOUNT_LIST:
            updateCount = mDb.update(ExpenseContract.Account.TABLE_NAME, values, whereClause, whereArgs);
            break;

        case ACCOUNT_ID:
            String idString = uri.getLastPathSegment();
            String where = Accounts._ID + " = " + idString;

            if (!TextUtils.isEmpty(whereClause)) {
                where += " AND " + whereClause;
            }

            updateCount = mDb.update(ExpenseContract.Account.TABLE_NAME, values, where, whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // notify all listeners of changes:
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
        case ACCOUNT_LIST:
            return Accounts.CONTENT_TYPE;
        case ACCOUNT_ID:
            return Accounts.CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ExpenseContract.Account.TABLE_NAME);

//        if (TextUtils.isEmpty(sortOrder)) {
//            sortOrder = null;
//        }

        switch (URI_MATCHER.match(uri)) {
        case ACCOUNT_LIST:
            // all nice and well
            break;
        case ACCOUNT_ID:
            // limit query to one row at most:
            builder.appendWhere(Accounts._ID + " = " + uri.getLastPathSegment());
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        // if we want to be notified of any changes:
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

}
