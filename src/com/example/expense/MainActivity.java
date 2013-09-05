package com.example.expense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //quickInsert(this);
        
        Intent intent = new Intent(this, SummaryActivity.class);
        startActivity(intent);
        
        finish();
    }
    
//    private static void quickInsert(Context context) {
//        ExpenseDbHelper expenseDbHelper = new ExpenseDbHelper(context);
//        SQLiteDatabase db = expenseDbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("column_name", "value");
//        db.insert("", null, values);
//        expenseDbHelper.close();
//    }
    
}
