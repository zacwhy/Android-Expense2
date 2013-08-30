package com.example.expense.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.common.MultiComparator;
import com.example.expense.helpers.Helper;
import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;
import com.example.expense.models.TransactionGroupDateComparator;
import com.example.expense.models.TransactionGroupSequenceComparator;

public final class TransactionHelper {
    
    private TransactionHelper() {}
    
    public static void insertTransactionGroup(Context context, TransactionGroup transactionGroup) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.insertLast(transactionGroup);
        
        databaseHelper.close();
    }
    
    public static void deleteTransactionGroup(Context context, long id) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        dataSource.deleteByTransactionGroupId(id);
        
        databaseHelper.close();
    }
    
    public static List<TransactionGroup> getTransactionGroups(Context context) {
        Map<Long, TransactionGroup> map = getTransactionGroupsMap(context);
        
        List<TransactionGroup> list = new ArrayList<TransactionGroup>(map.values());
        
        List<Comparator<TransactionGroup>> comparatorList = 
                new ArrayList<Comparator<TransactionGroup>>();
        
        comparatorList.add(new TransactionGroupDateComparator(true));
        comparatorList.add(new TransactionGroupSequenceComparator(true));
        
        Comparator<TransactionGroup> comparator = 
                new MultiComparator<TransactionGroup>(comparatorList);
        
        Collections.sort(list, comparator);
        
        return list;
    }
    
    private static Map<Long, TransactionGroup> getTransactionGroupsMap(Context context) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(database);
        Map<Long, TransactionGroup> transactionGroups = 
                dataSource.getTransactionGroupsWithTransactions(null, null);
        
        Map<Long, Account> accounts = Helper.getAccountsMap(database);
        Map<Long, ExpenseCategory> expenseCategories = Helper.getExpenseCategoriesMap(database);

        databaseHelper.close();
        
        for (TransactionGroup transactionGroup : transactionGroups.values()) {
            populateTransactionGroupChildren(transactionGroup, accounts, expenseCategories);
        }

        return transactionGroups;
    }

    public static void populateTransactionGroupChildren(TransactionGroup transactionGroup,
            Map<Long, Account> accounts, Map<Long, ExpenseCategory> expenseCategories) {
        
        long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
        ExpenseCategory expenseCategory = expenseCategories.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
        
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accounts.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
        
        for (Transaction transaction : transactionGroup.getTransactions()) {
            populateTransactionChildren(transaction, accounts);
        }
    }
    
    private static void populateTransactionChildren(Transaction transaction,
            Map<Long, Account> accounts) {
        
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accounts.get(toAccountId);
        transaction.setToAccount(toAccount);
    }

}
