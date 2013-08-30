package com.example.expense.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public final class InitialDataLoader {
    
    private InitialDataLoader() {}
	
	public static void initialize(Context context) {
		new InitialDataLoader(context).initializeData();
	}
	
	private Context mContext;
	
	private InitialDataLoader(Context context) {
		mContext = context;
	}

	public void initializeData() {
		ExpenseDatabaseHelper databaseHelper = new ExpenseDatabaseHelper(mContext);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		
        initializeAccounts(database);
		initializeExpenseCategories(database);
		
		databaseHelper.close();
	}
    
	private void initializeAccounts(SQLiteDatabase database) {
	    AccountsDataSource dataSource = new AccountsDataSource(database);

	    dataSource.deleteAll();

	    for (String title : Values.Accounts) {
	        dataSource.insert(title);
	    }
	}

	private void initializeExpenseCategories(SQLiteDatabase database) {
		ExpenseCategoriesDataSource dataSource = new ExpenseCategoriesDataSource(database);
		
		dataSource.deleteAll();
		
		for (String title : Values.Categories) {
			dataSource.createExpenseCategory(title);
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
