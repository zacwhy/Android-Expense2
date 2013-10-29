package com.example.expense;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DatabaseExport {

    //private final static String PackageName = "com.example.expense";
    //private static final String DatabaseName = "Expense.db";

    public static void export() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.example.expense//databases//Expense.db";
                String backupDBPath = "Expense.db";
                //String currentDBPath = "//data//{package name}//databases//{database name}";
                //String backupDBPath = "{database name}";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            // log
        }
    }

}
