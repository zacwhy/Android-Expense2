package com.example.common.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class StringHelper {

    private StringHelper() {}

    public static List<String> getRepeatedStringList(String s, int count) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            list.add(s);
        }
        return list;
    }

    // http://stackoverflow.com/a/187720
    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;                  
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    //    public static String repeat(String s, int count) {
    //        return new String(new char[count]).replace("\0", s);
    //    }

    public static String[] toStringArray(long[] values) {
        String[] stringArray = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            long value = values[i];
            stringArray[i] = String.valueOf(value);
        }

        return stringArray;
    }

}
