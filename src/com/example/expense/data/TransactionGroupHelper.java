package com.example.expense.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public final class TransactionGroupHelper {
    
    private TransactionGroupHelper() {}
    
    public static long insert(Context context, TransactionGroup transactionGroup) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        long id = dataSource.insertLast(transactionGroup);
        databaseHelper.close();
        return id;
    }
    
    public static void update(Context context, TransactionGroup transactionGroup) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        dataSource.update(transactionGroup);
        databaseHelper.close();
    }
    
    public static void deleteById(Context context, long id) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.deleteById(id);
        databaseHelper.close();
    }
    
    public static TransactionGroup getById(
            Context context,
            long id,
            boolean withTransactions,
            boolean withAccounts,
            boolean withExpenseCategory
            ) {
        
        TransactionGroup transactionGroup;
        Map<Long, Account> accountsMap = null;
        Map<Long, ExpenseCategory> expenseCategoriesMap = null;
        
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        
        if (withTransactions) {
            transactionGroup = dataSource.getWithTransactionsById(id);
        } else {
            transactionGroup = dataSource.getById(id);
        }
        
        if (withAccounts) {
            accountsMap = AccountHelper.getMap(db); // TODO improve efficiency
        }
        
        if (withExpenseCategory) {
            expenseCategoriesMap = ExpenseCategoryHelper.getMap(db); // TODO improve efficiency
        }
        
        databaseHelper.close();
        
        if (withAccounts) {
            long fromAccountId = transactionGroup.getFromAccount().getId();
            Account fromAccount = accountsMap.get(fromAccountId);
            transactionGroup.setFromAccount(fromAccount);
            
            if (withTransactions) {
                for (Transaction transaction : transactionGroup.getTransactions()) {
                    long toAccountId = transaction.getToAccount().getId();
                    Account toAccount = accountsMap.get(toAccountId);
                    transaction.setToAccount(toAccount);
                }
            }
        }
        
        if (withExpenseCategory) {
            long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
            ExpenseCategory expenseCategory = expenseCategoriesMap.get(expenseCategoryId);
            transactionGroup.setExpenseCategory(expenseCategory);
        }
        
        return transactionGroup;
    }
    
    public static List<TransactionGroup> getList(Context context) {
        Map<Long, TransactionGroup> map = getMap(context);
        List<TransactionGroup> list = new ArrayList<TransactionGroup>(map.values());
        return list;
    }
    
    public static Map<Long, TransactionGroup> getMap(Context context) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        Map<Long, TransactionGroup> transactionGroups = dataSource.getMapWithTransactions(null, null);
        Map<Long, Account> accountsMap = AccountHelper.getMap(db);
        Map<Long, ExpenseCategory> expenseCategoriesMap = ExpenseCategoryHelper.getMap(db);
        databaseHelper.close();
        
        for (TransactionGroup transactionGroup : transactionGroups.values()) {
            populateChildren(transactionGroup, accountsMap, expenseCategoriesMap);
        }

        return transactionGroups;
    }
    
    public static void populateChildren(
            TransactionGroup transactionGroup, 
            List<Account> accountsList, 
            List<ExpenseCategory> expenseCategoriesList) {
        
        Map<Long, Account> accountsMap = AccountHelper.convertListToMap(accountsList);
        Map<Long, ExpenseCategory> expenseCategoriesMap = ExpenseCategoryHelper.convertListToMap(expenseCategoriesList);
        populateChildren(transactionGroup, accountsMap, expenseCategoriesMap);
    }

    private static void populateChildren(
            TransactionGroup transactionGroup, 
            Map<Long, Account> accountsMap, 
            Map<Long, ExpenseCategory> expenseCategoriesMap) {
        
        populateExpenseCategory(transactionGroup, expenseCategoriesMap);
        populateFromAccount(transactionGroup, accountsMap);
        
        for (Transaction transaction : transactionGroup.getTransactions()) {
            TransactionHelper.populateToAccount(transaction, accountsMap);
        }
    }
    
    private static void populateExpenseCategory(TransactionGroup transactionGroup, Map<Long, ExpenseCategory> expenseCategoriesMap) {
        long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
        ExpenseCategory expenseCategory = expenseCategoriesMap.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
    }
    
    private static void populateFromAccount(TransactionGroup transactionGroup, Map<Long, Account> accountsMap) {
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accountsMap.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
    }

}
