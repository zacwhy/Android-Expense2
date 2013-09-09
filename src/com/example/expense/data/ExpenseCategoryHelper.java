package com.example.expense.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.example.expense.models.ExpenseCategory;

public final class ExpenseCategoryHelper {

    private ExpenseCategoryHelper() {}

    public static Map<Long, ExpenseCategory> convertListToMap(List<ExpenseCategory> list) {
        Map<Long, ExpenseCategory> map = new HashMap<Long, ExpenseCategory>();

        for (ExpenseCategory item : list) {
            map.put(item.getId(), item);
        }

        return map;
    }

    public static ExpenseCategory getById(SQLiteDatabase db, long id) {
        ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(db);
        return dataSource.getById(id);
    }

    public static Map<Long, ExpenseCategory> getMap(SQLiteDatabase db) {
        List<ExpenseCategory> list = getList(db);
        return convertListToMap(list);
    }

    public static List<ExpenseCategory> getList(SQLiteDatabase db) {
        ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(db);
        return dataSource.getList();
    }

}
