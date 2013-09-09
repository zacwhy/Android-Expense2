package com.example.expense.data;

import java.util.HashMap;
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
        long id = insert(db, transactionGroup);
        databaseHelper.close();
        return id;
    }

    private static long insert(SQLiteDatabase db, TransactionGroup transactionGroup) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        return dataSource.insertLast(transactionGroup);
    }

    public static int update(Context context, TransactionGroup transactionGroup) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsAffected = update(db, transactionGroup);
        databaseHelper.close();
        return rowsAffected;
    }

    private static int update(SQLiteDatabase db, TransactionGroup transactionGroup) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        return dataSource.update(transactionGroup);
    }

    public static int deleteById(Context context, long id) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsAffected = deleteById(db, id);
        databaseHelper.close();
        return rowsAffected;
    }

    private static int deleteById(SQLiteDatabase db, long id) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        return dataSource.deleteById(id);
    }

    public static TransactionGroup getById(Context context, long id, Map<Long, ExpenseCategory> expenseCategoryMap, Map<Long, Account> accountMap) {
        ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        TransactionGroup transactionGroup = getById(db, id, expenseCategoryMap, accountMap);
        databaseHelper.close();
        return transactionGroup;
    }

    public static TransactionGroup getById(SQLiteDatabase db, long id, Map<Long, ExpenseCategory> expenseCategoryMap, Map<Long, Account> accountMap) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        TransactionGroup transactionGroup = dataSource.getById(id);

        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(db);
        List<Transaction> transactionList = transactionsDataSource.getListByTransactionGroupId(id);

        populateExpenseCategory(transactionGroup, expenseCategoryMap);
        populateFromAccount(transactionGroup, accountMap);
        transactionGroup.setTransactions(transactionList);
        TransactionHelper.populateToAccount(transactionList, accountMap);

        return transactionGroup;
    }

    public static List<TransactionGroup> getList(SQLiteDatabase db, String selection, String[] selectionArgs, Map<Long, ExpenseCategory> expenseCategoryMap, Map<Long, Account> accountMap) {
        List<TransactionGroup> transactionGroupList = getList(db, selection, selectionArgs);
        long[] transactionGroupIdArray = TransactionGroupHelper.getIdArray(transactionGroupList);
        List<Transaction> transactionList = TransactionHelper.getListByTransactionGroupIdArray(db, transactionGroupIdArray);

        TransactionHelper.populateToAccount(transactionList, accountMap);
        populateExpenseCategory(transactionGroupList, expenseCategoryMap);
        populateFromAccount(transactionGroupList, accountMap);
        populateTransaction(transactionGroupList, transactionList);

        return transactionGroupList;
    }

    public static List<TransactionGroup> getList(SQLiteDatabase db, String selection, String[] selectionArgs) {
        TransactionGroupsDataSource dataSource = new TransactionGroupsDataSource(db);
        return dataSource.getList(selection, selectionArgs);
    }

    public static Map<Long, TransactionGroup> convertListToMap(List<TransactionGroup> list) {
        Map<Long, TransactionGroup> map = new HashMap<Long, TransactionGroup>();

        for (TransactionGroup item : list) {
            map.put(item.getId(), item);
        }

        return map;
    }

    public static long[] getIdArray(List<TransactionGroup> transactionGroupList) {
        long[] idArray = new long[transactionGroupList.size()];
        for (int i = 0; i < transactionGroupList.size(); i++) {
            TransactionGroup transactionGroup = transactionGroupList.get(i);
            idArray[i] = transactionGroup.getId();
        }
        return idArray;
    }

    public static void populateExpenseCategory(List<TransactionGroup> transactionGroupList, Map<Long, ExpenseCategory> expenseCategoryMap) {
        for (TransactionGroup transactionGroup : transactionGroupList) {
            populateExpenseCategory(transactionGroup, expenseCategoryMap);
        }
    }

    public static void populateExpenseCategory(TransactionGroup transactionGroup, Map<Long, ExpenseCategory> expenseCategoryMap) {
        long expenseCategoryId = transactionGroup.getExpenseCategory().getId();
        ExpenseCategory expenseCategory = expenseCategoryMap.get(expenseCategoryId);
        transactionGroup.setExpenseCategory(expenseCategory);
    }

    public static void populateFromAccount(List<TransactionGroup> transactionGroupList, Map<Long, Account> accountMap) {
        for (TransactionGroup transactionGroup : transactionGroupList) {
            populateFromAccount(transactionGroup, accountMap);
        }
    }

    public static void populateFromAccount(TransactionGroup transactionGroup, Map<Long, Account> accountMap) {
        long fromAccountId = transactionGroup.getFromAccount().getId();
        Account fromAccount = accountMap.get(fromAccountId);
        transactionGroup.setFromAccount(fromAccount);
    }

    public static void populateTransaction(List<TransactionGroup> transactionGroupList, List<Transaction> transactionList) {
        Map<Long, TransactionGroup> transactionGroupMap = convertListToMap(transactionGroupList);

        for (Transaction transaction : transactionList) {
            long transactionGroupId = transaction.getTransactionGroup().getId();
            TransactionGroup transactionGroup = transactionGroupMap.get(transactionGroupId);
            transactionGroup.getTransactions().add(transaction);
        }
    }

}
