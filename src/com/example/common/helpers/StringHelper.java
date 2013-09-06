package com.example.common.helpers;


public final class StringHelper {
    
    private StringHelper() {}

    public static String repeat(String s, int count) {
        return new String(new char[count]).replace("\0", s);
    }
    
}
