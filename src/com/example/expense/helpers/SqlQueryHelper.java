package com.example.expense.helpers;

import java.util.ArrayList;

public final class SqlQueryHelper {
    
    public static String format(String format, String... args) {
        ArrayList<String> list = new ArrayList<String>();
        
        for (String arg : args) {
            list.add(quote(arg));
        }
        
        return String.format(format, list.toArray());
    }

    private static String quote(String s) {
        return "\"" + s + "\"";
    }
   
}
