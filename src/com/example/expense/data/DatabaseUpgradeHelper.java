package com.example.expense.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class DatabaseUpgradeHelper {

    private DatabaseUpgradeHelper() {}
    
    public static void upgrade10To11(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE \"transaction_group\" ADD COLUMN \"from_account_id\" INTEGER");
        
        String[] columns = { "\"transaction_group_id\"", "\"from_account_id\"" };
        
        Cursor cursor = db.query("\"transaction\"", columns, null, null, null, null, null);
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a(cursor, db);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
    }
    
    private static void a(Cursor cursor, SQLiteDatabase db) {
        long transactionGroupId = cursor.getLong(0);
        long fromAccountId = cursor.getLong(1);
        
        ContentValues values = new ContentValues();
        values.put("\"from_account_id\"", fromAccountId);
        
        String whereClause = "\"_id\" = ?";
        String[] whereArgs = { Long.toString(transactionGroupId) };
        
        db.update("\"transaction_group\"", values, whereClause, whereArgs);
    }
    
}
