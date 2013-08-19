package com.example.expense.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class InitialDataLoader {
	
	public static void initialize(Context context) {
		new InitialDataLoader(context).initializeData();
	}
	
	private Context mContext;
	
	private InitialDataLoader(Context context) {
		mContext = context;
	}

	public void initializeData() {
		ExpenseDbHelper dbHelper = new ExpenseDbHelper(mContext);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		
        initializeAccounts(database);
		initializeExpenseCategories(database);
		//initializePaymentMethods(database);
		
		dbHelper.close();
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
	
//	private void initializePaymentMethods(SQLiteDatabase database) {
//		PaymentMethodsDataSource dataSource = new PaymentMethodsDataSource(database);
//		
//		dataSource.deleteAll();
//		
//		for (String title : Values.PaymentMethods) {
//			dataSource.insert(title);
//		}
//	}

	private final static class Values {
        
        public final static String[] Accounts = {
            "Expenditure",
            "Cash",
            "Ezlink",
            "Kopitiam",
            "SC BonusSaver",
            "Citi SMRT",
            "POSB Everyday",
            "POSB",
            "I", "M", "P", "R", "Other people"
        };
	    
	    public final static String[] Categories = {
	        "",
	        //"Food", "Transport", "Others",
	        "Breakfast", "Lunch", "Dinner", "Snack",
	        "Bus", "Train", "Bus (Work)", "Train (Work)",
	        "Fuel", "Road", "Parking",
	        //"I", "M", "F", "R", "Treat",
	        "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog."
	    };
	    
//	    public final static String[] PaymentMethods = {
//	        "Cash",
//	        "XtraSaver",
//	        "BonusSaver",
//	        "SMRT",
//	        "Everyday",
//	        "Ezlink",
//	        "Kopitiam",
//	        "I", "M", "P", "Other people",
//	        "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog."
//	    };
	    
	}
	
}
