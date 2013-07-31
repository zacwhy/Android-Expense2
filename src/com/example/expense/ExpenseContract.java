package com.example.expense;

import android.provider.BaseColumns;

public final class ExpenseContract {
	
	public ExpenseContract() {}
	
	public static abstract class ExpenseCategory implements BaseColumns {
        public static final String TABLE_NAME = "expense_category";
        public static final String COLUMN_NAME_EXPENSE_CATEGORY_ID = "expense_category_id";
        public static final String COLUMN_NAME_TITLE = "title";
    }
	
}
