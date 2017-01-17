package fr.todolist.todolist.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Stephane on 16/01/2017.
 */

public class StaticTools {

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

    /*public static String getUserFriendlyDateTime(int year, int month, int day, int hour, int minute) {
        String ret;
    }*/

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
     * Hide keyboard
     *
     * @param ctx   Current context
     * @param view  Current view
     */
    public static void hideKeyboard(Context ctx, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Check if keyboard is displayed
     * @param v The view
     * @return True or False
     */
    public static boolean keyboardIsDisplay(View v) {
        int heightDiff = v.getRootView().getHeight() - v.getHeight();
        return heightDiff > StaticTools.dpToPx(v.getContext(), 200);
    }

    /**
     * Convert dp to pixel
     *
     * @param ctx  Current context
     * @param valueInDp  the dp value in float
     */
    public static float dpToPx(Context ctx, float valueInDp) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
