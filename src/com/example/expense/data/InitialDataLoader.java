package com.example.expense.data;

import android.database.sqlite.SQLiteDatabase;

public final class InitialDataLoader {

    private InitialDataLoader() {}

    public static void initializeData(SQLiteDatabase db) {
        initializeAccounts(db);
        initializeExpenseCategories(db);
    }

    private static void initializeAccounts(SQLiteDatabase db) {
        AccountsDataSource dataSource = new AccountsDataSource(db);

        for (String name : Values.Accounts) {
            dataSource.insert(name);
        }
    }

    private static void initializeExpenseCategories(SQLiteDatabase db) {
        ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(db);

        for (String name : Values.Categories) {
            dataSource.insert(name);
        }
    }

    private final static class Values {

        public final static String[] Accounts = {
            "Expenditure",
            "Cash",
            "Ezlink",
            "Kopitiam",
            "SC BonusSaver",
            "SC Manhattan",
            "Citi SMRT",
            "POSB Everyday",
            "POSB",
            "I", "M", "P", "R", "Other people"
        };

        public final static String[] Categories = {
            "None",
            //"Food", "Transport", "Others",
            "Breakfast", "Lunch", "Dinner", "Snack",
            "Bus", "Train", "Bus (Work)", "Train (Work)",
            "Fuel", "Road", "Parking",
            "Others"
        };

    }

}
