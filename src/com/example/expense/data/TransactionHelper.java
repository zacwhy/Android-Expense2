package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;
import com.example.expense.models.Transaction;
import com.example.expense.models.TransactionGroup;

public final class TransactionHelper {

    private TransactionHelper() {}

    public static Map<Long, Transaction> convertToMap(List<Transaction> list) {
        Map<Long, Transaction> map = new HashMap<Long, Transaction>();

        for (Transaction item : list) {
            map.put(item.getId(), item);
        }

        return map;
    }

    public static long[] getIdArray(List<Transaction> transactionList) {
        long[] idArray = new long[transactionList.size()];
        for (int i = 0; i < transactionList.size(); i++) {
            Transaction transaction = transactionList.get(i);
            idArray[i] = transaction.getId();
        }
        return idArray;
    }

    public static List<Transaction> getListByTransactionGroupIdArray(SQLiteDatabase db, long[] transactionGroupIdArray) {
        TransactionsDataSource transactionsDataSource = new TransactionsDataSource(db);
        return transactionsDataSource.getListByTransactionGroupIds(transactionGroupIdArray);
    }

    public static void populateToAccount(List<Transaction> transactionList, Map<Long, Account> accountMap) {
        for (Transaction transaction : transactionList) {
            populateToAccount(transaction, accountMap);
        }
    }

    public static void populateToAccount(Transaction transaction, Map<Long, Account> accountMap) {
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accountMap.get(toAccountId);
        transaction.setToAccount(toAccount);
    }
    
    public static void setTransactionGroup(List<Transaction> transactionList, TransactionGroup transactionGroup) {
        for (Transaction transaction : transactionList) {
            transaction.setTransactionGroup(transactionGroup);
        }
    }

}
