package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.ExpenseCategory;

public final class ExpenseCategoryHelper {
    
    private ExpenseCategoryHelper() {}
    
    public static List<ExpenseCategory> getList(SQLiteDatabase database) {
        ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(database);
        return dataSource.getAll();
    }
    
    public static Map<Long, ExpenseCategory> getMap(SQLiteDatabase database) {
        List<ExpenseCategory> list = getList(database);
        return convertListToMap(list);
    }
    
    public static Map<Long, ExpenseCategory> convertListToMap(List<ExpenseCategory> list) {
        Map<Long, ExpenseCategory> map = new HashMap<Long, ExpenseCategory>();
        
        for (ExpenseCategory item : list) {
            map.put(item.getId(), item);
        }
        
        return map;
    }
    
}
