package com.example.expense.data;

import android.provider.BaseColumns;

public final class ExpenseContract {

    private ExpenseContract() {}

    public static abstract class ExpenseCategory implements BaseColumns {
        public static final String TABLE_NAME = "expense_category";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    public static abstract class PaymentMethod implements BaseColumns {
        public static final String TABLE_NAME = "payment_method";
        public static final String COLUMN_NAME_TITLE = "title";
    }

    public static abstract class Payment implements BaseColumns {
        public static final String TABLE_NAME = "payment";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PAYMENT_METHOD = "payment_method";
        public static final String COLUMN_NAME_EXPENSE_CATEGORY = "expense_category";
    }
    
    
    public static abstract class Account implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_NAME = "name";
    }
    
    public static abstract class TransactionGroup implements BaseColumns {
        public static final String TABLE_NAME = "transaction_group";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SEQUENCE = "sequence";
        public static final String COLUMN_NAME_EXPENSE_CATEGORY_ID = "expense_category_id";
        public static final String COLUMN_NAME_FROM_ACCOUNT_ID = "from_account_id";
    }

    public static abstract class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "transaction";
        public static final String COLUMN_NAME_TRANSACTION_GROUP_ID = "transaction_group_id";
        public static final String COLUMN_NAME_SEQUENCE = "sequence";
        public static final String COLUMN_NAME_TO_ACCOUNT_ID = "to_account_id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

}
