package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.expense.models.Account;
import com.example.expense.models.Transaction;

public final class TransactionHelper {
    
    private TransactionHelper() {}
    
    public static Map<Long, Transaction> convertToMap(List<Transaction> list) {
        Map<Long, Transaction> map = new HashMap<Long, Transaction>();
        
        for (Transaction item : list) {
            map.put(item.getId(), item);
        }
        
        return map;
    }
    
    public static void populateToAccount(Transaction transaction, Map<Long, Account> accounts) {
        long toAccountId = transaction.getToAccount().getId();
        Account toAccount = accounts.get(toAccountId);
        transaction.setToAccount(toAccount);
    }

}
