package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.Account;
import com.example.expense.models.ExpenseCategory;

public final class Helper {
    
    private Helper() {}
    
    public static Map<Long, Account> getAccountsMap(SQLiteDatabase database) {
        List<Account> list = Helper.getAccounts(database);
        return convertToAccountsMap(list);
    }
    
    public static List<Account> getAccounts(SQLiteDatabase database) {
        AccountsDataSource dataSource = new AccountsDataSource(database);
        return dataSource.getAll();
    }
    
//    public static List<PaymentMethod> getPaymentMethods(SQLiteDatabase database) {
//        PaymentMethodsDataSource dataSource = new PaymentMethodsDataSource(database);
//        return dataSource.getAll();
//    }
    
    public static Map<Long, ExpenseCategory> getExpenseCategoriesMap(SQLiteDatabase database) {
        List<ExpenseCategory> list = Helper.getExpenseCategories(database);
        return convertToExpenseCategoriesMap(list);
    }
    
    public static List<ExpenseCategory> getExpenseCategories(SQLiteDatabase database) {
        ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(database);
        return dataSource.getAll();
    }
    
    private static Map<Long, Account> convertToAccountsMap(List<Account> list) {
        Map<Long, Account> map = new HashMap<Long, Account>();
        
        for (Account item : list) {
            map.put(item.getId(), item);
        }
        
        return map;
    }
    
    private static Map<Long, ExpenseCategory> convertToExpenseCategoriesMap(List<ExpenseCategory> list) {
        Map<Long, ExpenseCategory> map = new HashMap<Long, ExpenseCategory>();
        
        for (ExpenseCategory item : list) {
            map.put(item.getId(), item);
        }
        
        return map;
    }
    
}
