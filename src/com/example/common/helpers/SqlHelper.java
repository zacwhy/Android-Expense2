package com.example.common.helpers;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class SqlHelper {

    private SqlHelper() {}

    public static String commaSeperatedQuestionMarks(int count) {
        List<String> stringList = StringHelper.getRepeatedStringList("?", count);
        return StringHelper.join(stringList, ", ");
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
    }

    public static String format(String format, String... args) {
        ArrayList<String> list = new ArrayList<String>();

        for (String arg : args) {
            list.add(quote(arg));
        }

        return String.format(format, list.toArray());
    }

    public static String leftOuterJoin(String table1, String key1, String table2, String key2) {
        return format(" LEFT OUTER JOIN %s ON %s.%s = %s.%s", table1, table1, key1, table2, key2);
    }

    public static String leftOuterJoin(String table1, String key1, String table2, String column2, String alias) {
        return format(" LEFT OUTER JOIN %s AS %s ON %s.%s = %s.%s", table1, alias, alias, key1, table2, column2);
    }

    public static String qualifiedColumn(String tableName, String columnName) {
        return format("%s.%s", tableName, columnName);
    }

    public static String quote(String s) {
        return "\"" + s + "\"";
    }

}
