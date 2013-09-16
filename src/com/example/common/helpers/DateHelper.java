package com.example.common.helpers;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public final class DateHelper {

    private DateHelper() {}

    public static String format(Calendar calendar, String pattern) {
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    public static Calendar getCalendarFromMilliseconds(long millis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    public static Calendar getCurrentDateOnly() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar;
    }

    public static String getDateTimeString(Calendar calendar) {
        return format(calendar, "yyyy-MM-dd HH:mm:ss Z");
    }

    public static String getDateWithDayOfWeekString(Calendar calendar) {
        return format(calendar, "yyyy-MM-dd E");
    }

    public static Calendar getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(getCurrentDateOnly());
    }

    public static String getShortDateString(Calendar calendar) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        return dateFormat.format(calendar.getTime());
    }

    public static Calendar getFirstDayOfMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static Calendar getUniversalTime(Calendar calendar) {
        long millis = calendar.getTimeInMillis();
        int offset = calendar.getTimeZone().getOffset(millis);
        return getCalendarFromMilliseconds(millis - offset);
    }

}
