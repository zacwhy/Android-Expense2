package com.example.expense.data;

import android.provider.BaseColumns;

public final class ExpenseContract {
	
	public ExpenseContract() {}
	
	public static abstract class ExpenseCategory implements BaseColumns {
        public static final String TABLE_NAME = "expense_category";
        public static final String COLUMN_NAME_TITLE = "title";
    }
	
	public static abstract class PaymentMethod implements BaseColumns {
		public static final String TABLE_NAME = "payment_method";
		public static final String COLUMN_NAME_TITLE = "title";
	}
	
//	public static abstract class ExpenseEntry implements BaseColumns {
//        public static final String TABLE_NAME = "expense_entry";
//        public static final String COLUMN_NAME_DATE = "date";
//        public static final String COLUMN_NAME_DESCRIPTION = "description";
//        public static final String COLUMN_NAME_CATEGORY_ID = "";
//        public static final String COLUMN_NAME_PAYMENT_ID = "";
//	}
	
}
