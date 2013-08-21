package com.example.expense.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.expense.helpers.DateHelper;
import com.example.expense.helpers.SqlQueryHelper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public class TransactionHelper {
    
    private SQLiteDatabase database;

    public TransactionHelper(SQLiteDatabase database) {
        this.database = database;
    }
    
    public long insert(Transaction transaction) {
        TransactionGroup transactionGroup = transaction.getTransactionGroup();
        
        long transactionGroupId = insertTransactionGroup(transactionGroup);
        transactionGroup.setId(transactionGroupId);
        
        long transactionId = insertTransaction(transaction);
        return transactionId;
    }
    
    private long insertTransactionGroup(TransactionGroup transactionGroup) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        
        Calendar date = transactionGroup.getDate();
        int maxSequence = dataSource.getMaxSequence(date);
        int sequence = maxSequence + 1;
        transactionGroup.setSequence(sequence);
        
        return dataSource.insert(transactionGroup);
    }
    
    private long insertTransaction(Transaction transaction) {
        TransactionsDataSource dataSource = new TransactionsDataSource(database);
        return dataSource.insert(transaction);
    }
    
    public List<Transaction> getTransactions() {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        
        
        String inTables = SqlQueryHelper.format("%s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.Transaction.TABLE_NAME,
                ExpenseContract.Transaction.COLUMN_NAME_TRANSACTION_GROUP_ID,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup._ID);
        
        sqliteQueryBuilder.setTables(inTables);
        
        
        String[] projectionIn = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        
        String sortOrder = SqlQueryHelper.format("%s.%s DESC, %s.%s DESC",
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_DATE,
                ExpenseContract.TransactionGroup.TABLE_NAME,
                ExpenseContract.TransactionGroup.COLUMN_NAME_SEQUENCE);
        
        Cursor cursor = sqliteQueryBuilder.query(database, projectionIn, selection, selectionArgs, 
                groupBy, having, sortOrder);
        
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);
        
        
        List<Transaction> items = new ArrayList<Transaction>();
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Transaction item = cursorToTransaction(cursor, accounts, expenseCategories);
            items.add(item);
            cursor.moveToNext();
        }
        
        // Make sure to close the cursor
        cursor.close();
        return items;
    }
    
    private Transaction cursorToTransaction(Cursor cursor, Map<Long, Account> accounts, 
            Map<Long, ExpenseCategory> expenseCategories) {
        long transactionId = cursor.getLong(0);
        long transactionGroupId = cursor.getLong(1);
        int transactionSequence = cursor.getInt(2);
        long fromAccountId = cursor.getLong(3);
        long toAccountId = cursor.getLong(4);
        BigDecimal amount = new BigDecimal(cursor.getString(5));
        String description = cursor.getString(6);
        
        //long transactionGroupId2 = cursor.getLong(7); // TODO
        Calendar date = DateHelper.getCalendarFromMilliseconds(cursor.getLong(8));
        int transactionGroupSequence = cursor.getInt(9);
        long expenseCategoryId = cursor.getLong(10);
        
        TransactionGroup transactionGroup = new TransactionGroup();
        transactionGroup.setId(transactionGroupId);
        transactionGroup.setDate(date);
        transactionGroup.setSequence(transactionGroupSequence);
        
        ExpenseCategory expenseCategory = expenseCategories.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
        
        Transaction transaction = new Transaction();
        
        transaction.setId(transactionId);
        transaction.setTransactionGroup(transactionGroup);
        transaction.setSequence(transactionSequence);
        
        Account fromAccount = accounts.get(fromAccountId);
        transaction.setFromAccount(fromAccount);
        
        Account toAccount = accounts.get(toAccountId);
        transaction.setToAccount(toAccount);
        
        transaction.setAmount(amount);
        transaction.setDescription(description);

        return transaction;
    }

}
