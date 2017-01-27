package fr.todolist.todolist.utils;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import fr.todolist.todolist.R;

/**
 * Created by Stephane on 18/01/2017.
 */

/**
 * Utility class for manage the date time
 */
public class DateTimeManager {

    private static String[] MonthArray = new String[] {
            "Jan", "Fev", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
    };

    private static String[] DaysSuffix = new String[] {
            "st", "nd", "rd"
    };

    /**
     * Cache a string date time into a long unix time
     * @param dateTime The date time
     * @return The unix time
     */
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

    /**
     * Apply unix time to a To do item info structure
     * @param info The to do item info structure
     * @param ms The unix time
     * @return The to do item info filled
     */
    public static TodoItemInfo retrieveDataTime(TodoItemInfo info, long ms) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(ms);
        info.year = cal.get(Calendar.YEAR);
        info.month = cal.get(Calendar.MONTH);
        info.day = cal.get(Calendar.DAY_OF_MONTH);
        info.hour = cal.get(Calendar.HOUR_OF_DAY);
        info.minute = cal.get(Calendar.MINUTE);
        return (info);
    }

    /**
     * Format the date time with date
     * @param year The year
     * @param month The month
     * @param day The day
     * @param hour The hour
     * @param minute The minute
     * @return The formated string
     */
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

    /**
     * Apply date time to a to do item info structure
     * @param info The structure
     * @param dateTime the date time
     * @return The structure filled
     */
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

    /**
     * Convert a value with a type to unix time
     * @param value The value
     * @param type The type
     * @return The unix time
     */
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

    /**
     * Cast Unix time in dayOfMonth for exemeple
     * @param type The type
     * @param value The valye
     * @return The ms
     */
    public static long getValueFromMs(String type, long value) {
        long ret = 0;

        if (type.equals("Second(s)")) {
            ret = value / 1000;
        } else if (type.equals("Minute(s)")) {
            ret = value / 1000 / 60;
        } else if (type.equals("Hour(s)")) {
            ret = value / 1000 / 60 / 60;
        } else if (type.equals("Day(s)")) {
            ret = value / 1000 / 60 / 60 / 24;
        }

        return (ret);
    }

    /**
     * Check if the unix time is not in the past
     * @param time The unix time
     * @return True or false
     */
    public static boolean isDateTimeValid(long time) {
        return (Calendar.getInstance().getTimeInMillis() < time);
    }

    /**
     * Get user friendly date time
     * @param context The context
     * @param date The date
     * @param year The year
     * @param month The month
     * @param day The day
     * @param hour The houyr
     * @param minute The minute
     * @return The date time formatted
     */
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

            int nbHours = Hours.hoursBetween(today, dateTime).getHours();
            int nbMinutes = Minutes.minutesBetween(today, dateTime).getMinutes();

            String tmpHour = dateTime.getHourOfDay() + "";
            String tmpMin = dateTime.getMinuteOfHour() + "";
            if (tmpHour.length() == 1) {
                tmpHour = "0" + tmpHour;
            }
            if (tmpMin.length() == 1) {
                tmpMin = "0" + tmpMin;
            }

            if (nbHours == 0) {
                if (nbMinutes == 0) {
                    ret = context.getString(R.string.date_transform_seconds);
                } else if (nbMinutes > 0) {
                    ret = context.getString(R.string.date_transform_minutes, nbMinutes);
                } else if (nbMinutes < 0) {
                    ret = "Today, " + tmpHour + ":" + tmpMin;
                }
            } else if (dateTime.toLocalDate().equals(today.toLocalDate())) {
                ret = "Today, " + tmpHour + ":" + tmpMin;
            } else if (dateTime.toLocalDate().equals(tomorrow.toLocalDate())) {
                ret = "Tomorrow, " + tmpHour + ":" + tmpMin;
            } else {
                ret = stringDay + " " + stringMonth + " " + stringYear + ", " + tmpHour + ":" + tmpMin;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

    /**
     * Cast int day to string day
     * @param day The day
     * @return The day in string
     */
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

    public static String getYear(int year) {
        return (String.valueOf(year));
    }

    public static boolean isToday(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();

        return (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
                    && cal.get(Calendar.DAY_OF_MONTH) == day);
    }

    public static boolean isTomorrow(int year, int month, int day) {
        boolean ret = false;
        String date = formatDateTime(year, month, day, 0, 0);

        try {
            Date tmp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(date);
            DateTime dateTime = new DateTime(tmp.getTime());
            DateTime today = new DateTime();
            DateTime tomorrow = today.plusDays(1);

            ret = dateTime.toLocalDate().equals(tomorrow.toLocalDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (ret);
    }

}
