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
    
    // TODO change to insertTransactionGroup
    public static void insertTransaction(Context context, Transaction transaction) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
        transactionsDataSource.insertIncludeParent(transaction);
        
        dbHelper.close();
    }
    
    public static void deleteTransactionGroup(Context context, TransactionGroup transactionGroup) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionGroupsDataSource transactionGroupsDataSource = 
                new TransactionGroupsDataSource(database);
        
        transactionGroupsDataSource.deleteByTransactionGroup(transactionGroup);
        
        dbHelper.close();
    }
    
    public static Transaction getTransaction(Context context, long id) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);
        
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
        Transaction transaction = transactionsDataSource.getTransaction(id, accounts, 
                expenseCategories);

        dbHelper.close();

        return transaction;
    }
    
    public static List<Transaction> getTransactions(Context context) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);

        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(database);
        List<Transaction> transactions = transactionsDataSource.getTransactions(accounts, 
                expenseCategories);

        dbHelper.close();

        return transactions;
    }

}
