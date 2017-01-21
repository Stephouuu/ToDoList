package fr.todolist.todolist.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephane on 16/01/2017.
 */

public class StaticTools {

    /**
     * Hide keyboard
     *
     * @param ctx  Current context
     * @param view Current view
     */
    public static void hideKeyboard(Context ctx, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * Check if keyboard is displayed
     *
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
     * @param ctx       Current context
     * @param valueInDp the dp value in float
     */
    public static float dpToPx(Context ctx, float valueInDp) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static List<TodoItemInfo> applyFilter(List<TodoItemInfo> list, TodoItemFilter filter) {
        List<TodoItemInfo> result = new ArrayList<>();
        boolean predicate;

        for (TodoItemInfo item : list) {
            //predicate = (!filter.hasFlags(TodoItemFilter.STATUS_EXPIRED) && item.status == TodoItemInfo.Status.Expired);
            Log.i("filter", item.title + ": " + item.status.toString());
            predicate = filter.hasFlags(item.status.getValue());
            if (predicate) {
                result.add(item);
            }
        }
        return (result);
    }

}
