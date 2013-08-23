package com.example.expense.helpers;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public final class DateHelper {

    //
    // Display
    //
    
    public static String getShortDateString(Calendar date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        return dateFormat.format(date.getTime());
    }
    
    public static String getDateWithDayOfWeekString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd E").format(date.getTime());
    }
    
    public static String getDateTimeString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(date.getTime());
    }

    
    //
    // SQL
    //
    
    public static String getSqlDateString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }
    
    public static String getSqlDateTimeString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
    }
    
    
    //
    //
    //
    
    public static Calendar getCurrentDateOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
    
    public static Calendar getCalendarFromMilliseconds(long millis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
    
    public static Calendar getUniversalTime(Calendar calendar) {
        int offset = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        long millis = calendar.getTimeInMillis() - offset;
        Calendar universalTimeCalendar = getCalendarFromMilliseconds(millis);
        return universalTimeCalendar;
    }

}
