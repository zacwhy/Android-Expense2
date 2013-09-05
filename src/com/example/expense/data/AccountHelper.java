package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;

public final class AccountHelper {
    
    private AccountHelper() {}
    
    public static List<Account> getList(SQLiteDatabase database) {
        AccountsDataSource dataSource = new AccountsDataSource(database);
        return dataSource.getAll();
    }
    
    public static Map<Long, Account> getMap(SQLiteDatabase database) {
        List<Account> list = getList(database);
        return convertListToMap(list);
    }
    
    public static Map<Long, Account> convertListToMap(List<Account> list) {
        Map<Long, Account> map = new HashMap<Long, Account>();
        
        for (Account item : list) {
            map.put(item.getId(), item);
        }
        
        return map;
    }
    
}
