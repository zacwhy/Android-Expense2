package com.example.expense.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.common.helpers.DateHelper;
import com.example.common.helpers.SqlHelper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class TransactionGroupsDataSource {

    public static final String TABLE_TO_ACCOUNT = "to_account";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String QUOTED_TABLE_NAME = SqlHelper.quote(ExpenseContract.TransactionGroup.TABLE_NAME);
    
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    
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

    private static final String[] sAllColumns = {
        ExpenseContract.TransactionGroup._ID,
        ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
        ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE,
        ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID,
        ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID
    };

    private SQLiteDatabase mDb;

    public TransactionGroupsDataSource(SQLiteDatabase db) {
        mDb = db;
    }

    public long insertLast(TransactionGroup transactionGroup) {
        TransactionHelper.setTransactionGroup(transactionGroup.getTransactions(), transactionGroup);
        int sequence = getMaxSequence(transactionGroup.getDate()) + 1;
        transactionGroup.setSequence(sequence);
        long transactionGroupId = insert(transactionGroup);
        transactionGroup.setId(transactionGroupId);
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(mDb);
        transactionsDataSource.insert(transactionGroup.getTransactions());
        return transactionGroupId;
    }

    private long insert(TransactionGroup transactionGroup) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_DATE, transactionGroup.getDate().getTimeInMillis());
        values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE, transactionGroup.getSequence());
        values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID, transactionGroup.getExpenseCategory().getId());
        values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID, transactionGroup.getFromAccount().getId());
        return mDb.insert(QUOTED_TABLE_NAME, null, values);
    }

    public int update(TransactionGroup transactionGroup) {
        TransactionGroup oldValue = getWithTransactionsById(transactionGroup.getId());
        return update(transactionGroup, oldValue);
    }

    public int update(TransactionGroup newValue, TransactionGroup oldValue) {
        int rowsAffected = 0;

        ContentValues values = getUpdateContentValues(newValue, oldValue);

        if (values.size() > 0) {
            rowsAffected = updateById(newValue.getId(), values);
        }

        List<Transaction> transactionsToInsert = new ArrayList<Transaction>();
        List<Transaction> transactionsToUpdate = new ArrayList<Transaction>();
        List<Transaction> transactionsToDelete = new ArrayList<Transaction>();
        Map<Long, Transaction> oldTransactionsMap = TransactionHelper.convertToMap(oldValue.getTransactions());
        Map<Long, Transaction> newTransactionsMap = TransactionHelper.convertToMap(newValue.getTransactions());

        for (Transaction transaction : oldValue.getTransactions()) {
            if (!newTransactionsMap.containsKey(transaction.getId())) {
                transactionsToDelete.add(transaction);
            }
        }

        for (Transaction transaction : newValue.getTransactions()) {
            if (transaction.getId() > 0) {
                transactionsToUpdate.add(transaction);
            } else {
                transactionsToInsert.add(transaction);
            }
        }

        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(mDb);
        rowsAffected += transactionsDataSource.delete(transactionsToDelete);
        rowsAffected += transactionsDataSource.insert(transactionsToInsert);

        for (Transaction transaction : transactionsToUpdate) {
            Transaction oldTransaction = oldTransactionsMap.get(transaction.getId());
            rowsAffected += transactionsDataSource.update(transaction, oldTransaction);
        }

        return rowsAffected;
    }

    private ContentValues getUpdateContentValues(TransactionGroup newValue, TransactionGroup oldValue) {
        ContentValues values = new ContentValues();

        if (newValue.getDate().compareTo(oldValue.getDate()) != 0) {
            values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_DATE, newValue.getDate().getTimeInMillis());
        }

        if (newValue.getExpenseCategory().getId() != oldValue.getExpenseCategory().getId()) {
            values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_EXPENSE_CATEGORY_ID, newValue.getExpenseCategory().getId());
        }

        if (newValue.getFromAccount().getId() != oldValue.getFromAccount().getId()) {
            values.put(ExpenseContract.TransactionGroup.COLUMN_NAME_FROM_ACCOUNT_ID, newValue.getFromAccount().getId());
        }

        return values;
    }

    public int updateById(long id, ContentValues values) {
        String whereClause = ExpenseContract.TransactionGroup._ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        return update(values, whereClause, whereArgs);
    }

    private int update(ContentValues values, String whereClause, String[] whereArgs) {
        return mDb.update(QUOTED_TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(long id) {
        // TODO delete in a transaction
        // Delete child records first
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(mDb);
        transactionsDataSource.deleteByTransactionGroupId(id);

        String whereClause = ExpenseContract.TransactionGroup._ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(id) };
        return delete(whereClause, whereArgs);
    }

    private int delete(String whereClause, String[] whereArgs) {
        return mDb.delete(QUOTED_TABLE_NAME, whereClause, whereArgs);
    }

    public TransactionGroup getWithTransactionsById(long id) {
        TransactionGroup transactionGroup = getById(id);

        if (transactionGroup == null) {
            return null;
        }

        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(mDb);
        List<Transaction> transactions = transactionsDataSource.getListByTransactionGroupId(id);
        TransactionHelper.setTransactionGroup(transactions, transactionGroup);
        transactionGroup.setTransactions(transactions);
        return transactionGroup;
    }

    public TransactionGroup getById(long id) {
        String selection = ExpenseContract.TransactionGroup._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        List<TransactionGroup> list = getList(selection, selectionArgs);

        if (list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    public List<TransactionGroup> getList(String selection, String[] selectionArgs) {
        Cursor cursor = getCursor(selection, selectionArgs);
        List<TransactionGroup> list = new ArrayList<TransactionGroup>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            TransactionGroup item = cursorToTransactionGroup(cursor);
            list.add(item);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    private Cursor getCursor(String selection, String[] selectionArgs) {
        return mDb.query(QUOTED_TABLE_NAME, sAllColumns, selection, selectionArgs, null, null, null);
    }

    private TransactionGroup cursorToTransactionGroup(Cursor cursor) {
        long id = cursor.getLong(0);
        Calendar date = DateHelper.getCalendarFromMilliseconds(cursor.getLong(1));
        int sequence = cursor.getInt(2);
        ExpenseCategory expenseCategory = new ExpenseCategory(cursor.getLong(3));
        Account fromAccount = new Account(cursor.getLong(4));

        TransactionGroup transactionGroup = new TransactionGroup(id);
        transactionGroup.setDate(date);
        transactionGroup.setSequence(sequence);
        transactionGroup.setExpenseCategory(expenseCategory);
        transactionGroup.setFromAccount(fromAccount);

        return transactionGroup;
    }

    private int getMaxSequence(Calendar calendar) {
        int sequence = 0;

        String sql = SqlHelper.format("SELECT MAX(%s) FROM %s" +
                " WHERE datetime(%s/1000, 'unixepoch') BETWEEN ? AND datetime(?, '+1 day')",
                ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_DATE);

        Calendar universalTimeCalendar = DateHelper.getUniversalTime(calendar);
        String dateTimeString = SqlHelper.getDateTimeString(universalTimeCalendar);
        Cursor cursor = mDb.rawQuery(sql, new String[] { dateTimeString, dateTimeString }); 
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sequence = cursor.getInt(0);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        return sequence;
    }
    
    public List<Calendar> getDistinctCalendars() {
        List<Calendar> calendars = new ArrayList<Calendar>();
        
        String sql = "SELECT DISTINCT" +
                " strftime('%Y',datetime(" + SqlHelper.quote(ExpenseContract.TransactionGroup.COLUMN_NAME_DATE) + "/1000, 'unixepoch')) AS " + SqlHelper.quote(YEAR) +
                ", strftime('%m',datetime(" + SqlHelper.quote(ExpenseContract.TransactionGroup.COLUMN_NAME_DATE) + "/1000, 'unixepoch')) AS " + SqlHelper.quote(MONTH) +
                " FROM " + SqlHelper.quote(ExpenseContract.TransactionGroup.TABLE_NAME) +
                " ORDER BY " + SqlHelper.quote(YEAR) + ", " + SqlHelper.quote(MONTH);
        
        Cursor cursor = mDb.rawQuery(sql, null); 
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int year = Integer.parseInt(cursor.getString(0));
            int month = Integer.parseInt(cursor.getString(1)) - 1;
            Calendar calendar = new GregorianCalendar(year, month, 1);
            calendars.add(calendar);
            cursor.moveToNext();
        }

        cursor.close();
        return calendars;
    }
    
    public Cursor queryGroupByCategory(String selection, String[] selectionArgs, String sortOrder) {
        return queryGroupByCategory(mDb, selection, selectionArgs, sortOrder);
    }

    private static Cursor queryGroupByCategory(SQLiteDatabase db, String selection, String[] selectionArgs, String sortOrder) {
        String inTables = SqlHelper.quote(ExpenseContract.Transaction.TABLE_NAME) +
                SqlHelper.leftOuterJoin(TRANSACTION_GROUP, TRANSACTION_GROUP_ID, TRANSACTION, TRANSACTION_TRANSACTION_GROUP_ID) +
                SqlHelper.leftOuterJoin(EXPENSE_CATEGORY, EXPENSE_CATEGORY_ID, TRANSACTION_GROUP, TRANSACTION_GROUP_EXPENSE_CATEGORY_ID) +
                SqlHelper.leftOuterJoin(ACCOUNT, ACCOUNT_ID, TRANSACTION, TRANSACTION_TO_ACCOUNT_ID, TABLE_TO_ACCOUNT);
    
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
