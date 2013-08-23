package com.example.expense.helpers;

import java.util.ArrayList;

public final class SqlHelper {
    
    public static String format(String format, String... args) {
        ArrayList<String> list = new ArrayList<String>();
        
        for (String arg : args) {
            list.add(quote(arg));
        }
        
        return String.format(format, list.toArray());
    }

    public static String quote(String s) {
        return "\"" + s + "\"";
    }
   
}
