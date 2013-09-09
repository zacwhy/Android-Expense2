package com.example.common.helpers;

import java.util.ArrayList;
import java.util.List;

public final class SqlHelper {

    private SqlHelper() {}

    public static String commaSeperatedQuestionMarks(int count) {
        List<String> stringList = StringHelper.getRepeatedStringList("?", count);
        return StringHelper.join(stringList, ", ");
    }

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
