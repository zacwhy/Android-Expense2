package com.example.expense.data;

import android.database.sqlite.SQLiteDatabase;

public final class InitialDataLoader {
    
    private InitialDataLoader() {}
	
	public static void initializeData(SQLiteDatabase database) {
	    initializeAccounts(database);
        initializeExpenseCategories(database);
	}
    
	private static void initializeAccounts(SQLiteDatabase database) {
	    AccountsDataSource dataSource = new AccountsDataSource(database);

	    dataSource.deleteAll();

	    for (String title : Values.Accounts) {
	        dataSource.insert(title);
	    }
	}

	private static void initializeExpenseCategories(SQLiteDatabase database) {
		ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(database);
		
		dataSource.deleteAll();
		
		for (String title : Values.Categories) {
			dataSource.insert(title);
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
