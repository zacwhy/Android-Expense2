package com.zacwhy.expense.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.common.helpers.SqlHelper;
import com.example.expense.data.ExpenseContract;
import com.example.expense.data.ExpenseDatabaseHelper;

public class SummaryProvider extends ContentProvider {

    public static final String AUTHORITY = "com.zacwhy.expense.provider.summaries";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + Summaries.CONTENT_PATH);

    public static final String TABLE_TO_ACCOUNT = "to_account";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_AMOUNT = "amount";

    private static final String TRANSACTION = ExpenseContract.Transaction.TABLE_NAME;
    private static final String TRANSACTION_TRANSACTION_GROUP_ID = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID;
    private static final String TRANSACTION_TO_ACCOUNT_ID = ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID;
    private static final String TRANSACTION_GROUP = ExpenseContract.TransactionGroup.TABLE_NAME;
    private static final String TRANSACTION_GROUP_ID = ExpenseContract.TransactionGroup._ID;
    private static final String TRANSACTION_GROUP_EXPENSE_CATEGORY_ID = ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID;
    private static final String EXPENSE_CATEGORY = ExpenseContract.ExpenseCategory.TABLE_NAME;
    private static final String EXPENSE_CATEGORY_ID = ExpenseContract.ExpenseCategory._ID;
    private static final String EXPENSE_CATEGORY_NAME = ExpenseContract.ExpenseCategory.COLUMN_NAME_TITLE;
    private static final String ACCOUNT = ExpenseContract.Account.TABLE_NAME;
    private static final String ACCOUNT_ID = ExpenseContract.Account._ID;

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

//        if (mDb.isReadOnly()) {
//            mDb.close();
//            mDb = null;
//            return false;
//        }

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
            cursor = queryGroupByCategory(mDb, selection, selectionArgs, sortOrder);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private static Cursor queryGroupByCategory(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        String inTables = SqlHelper.quote(ExpenseContract.Transaction.TABLE_NAME) +
                SqlHelper.leftOuterJoin(TRANSACTION_GROUP, TRANSACTION_GROUP_ID, TRANSACTION, TRANSACTION_TRANSACTION_GROUP_ID) +
                SqlHelper.leftOuterJoin(EXPENSE_CATEGORY, EXPENSE_CATEGORY_ID, TRANSACTION_GROUP, TRANSACTION_GROUP_EXPENSE_CATEGORY_ID) +
                SqlHelper.leftOuterJoin(ACCOUNT, ACCOUNT_ID, TRANSACTION, TRANSACTION_TO_ACCOUNT_ID, "to_account");

        String[] projectionIn = {
                SqlHelper.qualifiedColumn(EXPENSE_CATEGORY, EXPENSE_CATEGORY_ID),
                SqlHelper.qualifiedColumn(EXPENSE_CATEGORY, EXPENSE_CATEGORY_NAME) + " AS " + SqlHelper.quote(COLUMN_DESCRIPTION),
                SqlHelper.format("SUM(%s.%s) AS %s", TRANSACTION, ExpenseContract.Transaction.COLUMN_NAME_AMOUNT, COLUMN_AMOUNT)
        };

        String groupBy = SqlHelper.qualifiedColumn(TRANSACTION_GROUP, TRANSACTION_GROUP_EXPENSE_CATEGORY_ID);
        String having = null;

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(inTables);
        Cursor cursor = builder.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder);
        return cursor;
    }

}
