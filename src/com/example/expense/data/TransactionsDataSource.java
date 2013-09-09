package com.example.expense.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.common.helpers.SqlHelper;
import com.example.common.helpers.StringHelper;
import com.example.expense.models.Account;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class TransactionsDataSource {

    private static final String QUOTED_TABLE_NAME = SqlHelper.quote(ExpenseContract.Transaction.TABLE_NAME);

    private static final String[] sAllColumns = {
        ExpenseContract.Transaction._ID,
        ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID,
        ExpenseContract.Transaction.COLUMN_NAME_SEQUENCE,
        ExpenseContract.Transaction.COLUMN_NAME_TO_ACCOUNT_ID,
        ExpenseContract.Transaction.COLUMN_NAME_AMOUNT,
        ExpenseContract.Transaction.COLUMN_NAME_DESCRIPTION
    };

    private SQLiteDatabase mDb;

    public TransactionsDataSource(SQLiteDatabase db) {
        mDb = db;
    }

    public int insert(List<Transaction> transactionList) {
        int rowsAffected = 0;

        for (Transaction transaction : transactionList) {
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
        return mDb.insert(QUOTED_TABLE_NAME, null, values);
    }

    public int update(Transaction newValue, Transaction oldValue) {
        ContentValues values = getUpdateContentValues(newValue, oldValue);

        if (values.size() <= 0) {
            return 0;
        }

        return updateById(newValue.getId(), values);
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
        return mDb.update(QUOTED_TABLE_NAME, values, whereClause, whereArgs);
    }

    public int delete(List<Transaction> transactionList) {
        String s = SqlHelper.commaSeperatedQuestionMarks(transactionList.size());
        String whereClause = ExpenseContract.Transaction._ID + " IN (" + s + ")";

        long[] idArray = TransactionHelper.getIdArray(transactionList);
        String[] whereArgs = StringHelper.toStringArray(idArray);

        return delete(whereClause, whereArgs);
    }

    public int deleteByTransactionGroupId(long transactionGroupId) {
        String whereClause = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " = ?";
        String[] whereArgs = new String[] { Long.toString(transactionGroupId) };
        return delete(whereClause, whereArgs);
    }

    private int delete(String whereClause, String[] whereArgs) {
        return mDb.delete(QUOTED_TABLE_NAME, whereClause, whereArgs);
    }

    public List<Transaction> getListByTransactionGroupId(long transactionGroupId) {
        long[] transactionGroupIdArray = new long[] { transactionGroupId };
        return getListByTransactionGroupIds(transactionGroupIdArray);
    }

    public List<Transaction> getListByTransactionGroupIds(long[] transactionGroupIdArray) {
        String s = SqlHelper.commaSeperatedQuestionMarks(transactionGroupIdArray.length);
        String selection = ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID + " IN (" + s + ")";
        String[] selectionArgs = StringHelper.toStringArray(transactionGroupIdArray);
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
        return mDb.query(QUOTED_TABLE_NAME, sAllColumns, selection, selectionArgs, null, null, null);
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        long id = cursor.getLong(0);
        TransactionGroup transactionGroup = new TransactionGroup(cursor.getLong(1));
        int sequence = cursor.getInt(2);
        Account toAccount = new Account(cursor.getLong(3));
        BigDecimal amount = new BigDecimal(cursor.getString(4));
        String description = cursor.getString(5);

        Transaction transaction = new Transaction(id);
        transaction.setTransactionGroup(transactionGroup);
        transaction.setSequence(sequence);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transaction;
    }

}
