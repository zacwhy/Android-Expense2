package com.example.expense.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.expense.helpers.Helper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;
import com.example.expense.models.TransactionGroupListComparator;

public final class TransactionGroupHelper {
    
    private TransactionGroupHelper() {}
    
    public static void insert(Context context, TransactionGroup transactionGroup) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        dataSource.insertLast(transactionGroup);
        databaseHelper.close();
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
    
    public static List<TransactionGroup> getSortedList(Context context) {
        Map<Long, TransactionGroup> map = getMap(context);
        Comparator<TransactionGroup> comparator = new TransactionGroupListComparator(true);
        List<TransactionGroup> list = new ArrayList<TransactionGroup>(map.values());
        Collections.sort(list, comparator);
        return list;
    }
    
    private static Map<Long, TransactionGroup> getMap(Context context) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        Map<Long, TransactionGroup> transactionGroups = dataSource.getMapWithTransactions(null, null);
        
        Map<Long, Account> accounts = Helper.getAccountsMap(db);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(db);

        databaseHelper.close();
        
        for (TransactionGroup transactionGroup : transactionGroups.values()) {
            populateChildren(transactionGroup, accounts, expenseCategories);
        }

        return transactionGroups;
    }
    
    public static void populateChildren(TransactionGroup transactionGroup,
            List<Account> accounts, List<ExpenseCategory> expenseCategories) {
        
        Map<Long, Account> accountsMap = Helper.convertToAccountsMap(accounts);
        Map<Long, ExpenseCategory> expenseCategoriesMap = Helper.convertToExpenseCategoriesMap(expenseCategories);
        populateChildren(transactionGroup, accountsMap, expenseCategoriesMap);
    }

    public static void populateChildren(TransactionGroup transactionGroup,
            Map<Long, Account> accounts, Map<Long, ExpenseCategory> expenseCategories) {
        
        long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
        ExpenseCategory expenseCategory = expenseCategories.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
        
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accounts.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
        
        for (Transaction transaction : transactionGroup.getTransactions()) {
            TransactionHelper.populateChildren(transaction, accounts);
        }
    }

}
