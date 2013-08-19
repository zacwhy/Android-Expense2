package com.example.expense.helpers;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public final class DateHelper {

    public static String getShortDateString(Calendar date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        return dateFormat.format(date.getTime());
    }
    
    public static String getDateWithDayOfWeekString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd E").format(date.getTime());
    }
    
    public static String getSqlDateString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }
    
    public static String getDateTimeString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
    }
    
    public static Calendar getDate(long millis) {
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(millis);
        return date;
    }

}
