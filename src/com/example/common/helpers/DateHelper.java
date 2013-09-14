package com.example.common.helpers;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public final class DateHelper {

    private DateHelper() {}

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

    public static String getDateTimeString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(date.getTime());
    }

    public static String getDateWithDayOfWeekString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd E").format(date.getTime());
    }

    public static Calendar getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(getCurrentDateOnly());
    }

    public static String getShortDateString(Calendar date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        return dateFormat.format(date.getTime());
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
