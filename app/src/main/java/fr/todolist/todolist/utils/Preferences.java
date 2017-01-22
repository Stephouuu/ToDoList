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
 * Cette classe a pour fonction de gérer les préférences de l'application.
 */
public class Preferences {

    private final static String FILTER_STATUS = "FILTER.STATUS";

    /**
     * Retourne l'instance par défaut des préférences partagées.
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

}
