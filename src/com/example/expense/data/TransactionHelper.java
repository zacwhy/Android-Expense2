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
    
//    public static void updateTransactionGroup(Context context, long id, ContentValues values) {
//        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
//        
//        SQLiteDatabase database = dbHelper.getWritableDatabase();
//        
//        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
//        dataSource.updateById(id, values);
//        
//        dbHelper.close();
//    }
    
    public static void deleteTransactionGroup(Context context, TransactionGroup transactionGroup) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.deleteByTransactionGroup(transactionGroup);
        
        dbHelper.close();
    }
    
    public static Transaction getTransaction(Context context, long id) {
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);
        
        TransactionsDataSource dataSource = new TransactionsDataSource(database);
        Transaction transaction = dataSource.getTransaction(id, accounts, expenseCategories);

        dbHelper.close();

        return transaction;
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

}
