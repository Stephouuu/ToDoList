package fr.todolist.todolist.utils;

/**
 * Created by Stephane on 05/01/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class manage the user preferences
 */
public class Preferences {

    private final static String FILTER_STATUS = "FILTER.STATUS";
    private final static String SORTING_DATE = "SORTING.DATE";

    /**
     * Return the instance of the shared preferences
     *
     * @param context Le contexte
     * @return L'instance par défaut
     */
    @NonNull
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setHomePageFilter(Context context, TodoItemFilter filter) {
        SharedPreferences prefs = getSharedPreferences(context);
        if (filter == null) {
            prefs.edit().remove(FILTER_STATUS).apply();
        } else {
            prefs.edit().putInt(FILTER_STATUS, filter.status).apply();
        }
    }

    @Nullable
    public static TodoItemFilter getHomePageFilter(Context context) {
        TodoItemFilter filter = null;
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            int status = prefs.getInt(FILTER_STATUS, -1);
            if (status > -1) {
                filter = new TodoItemFilter();
                filter.setFlags(status);
            }
        } catch (Exception e) {
            setHomePageFilter(context, null);
        }
        return (filter);
    }

    public static void setHomePageSorting(Context context, SortingInfo info) {
        SharedPreferences prefs = getSharedPreferences(context);
        if (info == null) {
            prefs.edit().remove(SORTING_DATE).apply();
        } else {
            prefs.edit().putInt(SORTING_DATE, info.date.ordinal()).apply();
        }
    }

    @Nullable
    public static SortingInfo getHomePageSorting(Context context) {
        SortingInfo sorting = new SortingInfo();
        SharedPreferences prefs = getSharedPreferences(context);
        try {
            int date = prefs.getInt(SORTING_DATE, -1);
            sorting.date = (date == SortingInfo.Type.Descendant.ordinal()) ? SortingInfo.Type.Descendant : SortingInfo.Type.Ascendant;
        } catch (Exception e) {
            setHomePageFilter(context, null);
        }
        return (sorting);
    }

}
