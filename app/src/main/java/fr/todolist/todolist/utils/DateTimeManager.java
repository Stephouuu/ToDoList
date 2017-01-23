package fr.todolist.todolist.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Years;

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

    private static String[] MonthArray = new String[] {
            "Jan", "Fev", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
    };

    private static String[] DaysSuffix = new String[] {
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

    public static long getMs(int value, String type) {
        long ret = 0;

        if (type.equals("Second(s)")) {
            ret = 1000 * value;
        } else if (type.equals("Minute(s)")) {
            ret = 1000 * 60 * value;
        } else if (type.equals("Hour(s)")) {
            ret = 1000 * 60 * 60 * value;
        } else if (type.equals("Day(s)")) {
            ret = 1000 * 60 * 60 * 24 * value;
        }

        return (ret);
    }

    public static boolean isDateTimeValid(long time) {
        return (Calendar.getInstance().getTimeInMillis() < time);
    }

    public static String getUserFriendlyDateTime(Context context, String date, int year, int month,
                                                 int day, int hour, int minute) {

        String ret = "null";
        String stringYear = String.valueOf(year);
        String stringMonth = getMonth(month);
        String stringDay = getDay(day);

        try {
            Date tmp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(date);
            DateTime dateTime = new DateTime(tmp.getTime());
            DateTime today = new DateTime();
            DateTime tomorrow = today.plusDays(1);

            boolean thisYear = Years.yearsBetween(today, dateTime).getYears() == 0;

            int nbHours = Hours.hoursBetween(today, dateTime).getHours();
            int nbMinutes = Minutes.minutesBetween(today, dateTime).getMinutes();

            if (nbHours == 0) {
                if (nbMinutes == 0) {
                    ret = context.getString(R.string.date_transform_seconds);
                } else {
                    ret = context.getString(R.string.date_transform_minutes, nbMinutes);
                }
            } else if (nbHours < 24) {
                ret = context.getString(R.string.date_transform_hours, nbHours);
            } else if (dateTime.toLocalDate().equals(tomorrow.toLocalDate())) {
                ret = context.getString(R.string.date_transform_tomorrow);
            } else {
                if (thisYear) {
                    ret = stringDay + " " + stringMonth;
                } else {
                    ret = stringDay + " " + stringMonth + " " + stringYear;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

    public static String getDay(int day) {
        String str = String.valueOf(day);

        if (day - 1 < DaysSuffix.length) {
            str += DaysSuffix[day - 1];
        } else {
            str += "th";
        }
        return (str);
    }

    public static String getMonth(int month) {
        return (MonthArray[month]);
    }

}
