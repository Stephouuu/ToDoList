package fr.todolist.todolist.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import fr.todolist.todolist.R;

/**
 * Created by Stephane on 18/01/2017.
 */

public class DateTimeManager {

    private static String[] Months = new String[] {
            "Jan", "Fev", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
    };

    private static String[] Days = new String[] {
            "st", "nd", "rd"
    };

    public static long castDateTimeToUnixTime(String dateTime) {
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (date != null) {
            cal.setTime(date);
            return (cal.getTimeInMillis());
        }

        return (0);
    }

    public static String formatDateTime(int year, int month, int day, int hour, int minute) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static TodoItemInfo retrieveDateTime(TodoItemInfo info, String dateTime) {
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (date != null) {
            cal.setTime(date);
            info.year = cal.get(Calendar.YEAR);
            info.month = cal.get(Calendar.MONTH);
            info.day = cal.get(Calendar.DAY_OF_MONTH);
            info.hour = cal.get(Calendar.HOUR_OF_DAY);
            info.minute = cal.get(Calendar.MINUTE);
        }
        return (info);
    }

    public static String getUserFriendlyDateTime(Context context, int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        String ret = "";

        boolean sameYear = cal.get(Calendar.YEAR) == year;
        boolean sameMonth = cal.get(Calendar.MONTH) == month;
        boolean sameDay = cal.get(Calendar.DAY_OF_MONTH) == day;
        boolean sameHour = cal.get(Calendar.HOUR_OF_DAY) == hour;
        boolean sameMinute = cal.get(Calendar.MINUTE) == minute;

        int diffDays = day - cal.get(Calendar.DAY_OF_MONTH) - 1;
        int diffHours = hour - cal.get(Calendar.HOUR_OF_DAY) - 1;
        int diffMinutes = minute - cal.get(Calendar.MINUTE) - 1;

        String stringYear = String.valueOf(year);
        String stringMonth = getMonth(month);
        String stringDay = getDay(day);

        if (sameYear && sameMonth) {
            if (sameDay) {
                if (diffHours < 1) {
                    ret = String.format(context.getString(R.string.date_today_minutes), diffMinutes + 60);
                } else {
                    ret = String.format(context.getString(R.string.date_today_hours), diffHours);
                }
            } else {
                if (diffDays < 1) {
                    ret = String.format(context.getString(R.string.date_today_hours), diffHours + 24);
                } else {
                    ret = String.format(context.getString(R.string.date_days), diffDays);
                }
            }
        } else {
            if (sameYear) {
                ret = String.format(context.getString(R.string.date_time), stringDay, stringMonth, "");
            } else {
                ret = String.format(context.getString(R.string.date_time), stringDay, stringMonth, stringYear);
            }
        }

        return (ret);
    }

    private static String getDay(int day) {
        String str = String.valueOf(day);

        if (day - 1 < Days.length) {
            str += Days[day - 1];
        } else {
            str += "th";
        }
        return (str);
    }

    private static String getMonth(int month) {
        return (Months[month]);
    }

}
