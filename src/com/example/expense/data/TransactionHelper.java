package com.example.expense.data;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public final class TransactionHelper {
    
    private TransactionHelper() {}
    
    public static void insertTransactionGroup(Context context, TransactionGroup transactionGroup) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.insertLast(transactionGroup);
        
        dbHelper.close();
    }
    
    public static void deleteTransactionGroup(Context context, long id) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.deleteByTransactionGroupId(id);
        
        dbHelper.close();
    }
    
    public static List<Transaction> getTransactions(Context context) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);

        TransactionsDataSource dataSource = new TransactionsDataSource(database);
        List<Transaction> transactions = dataSource.getTransactions(accounts, expenseCategories);

        dbHelper.close();

        return transactions;
    }

    public static void populateTransactionGroupChildren(TransactionGroup transactionGroup,
            Map<Long, Account> accounts, Map<Long, ExpenseCategory> expenseCategories) {
        
        long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
        ExpenseCategory expenseCategory = expenseCategories.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
        
        for (Transaction transaction : transactionGroup.getTransactions()) {
            populateTransactionChildren(transaction, accounts);
        }
    }
    
    private static void populateTransactionChildren(Transaction transaction,
            Map<Long, Account> accounts) {
        
        long fromAccountId = transaction.getFromAccount().getId();
        Account fromAccount = accounts.get(fromAccountId);
        transaction.setFromAccount(fromAccount);
        
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accounts.get(toAccountId);
        transaction.setToAccount(toAccount);
    }

}
