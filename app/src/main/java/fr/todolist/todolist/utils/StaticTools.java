package fr.todolist.todolist.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static String[] deserializeFiles(String serialized, String sep) {
        return serialized.split(sep);
    }

    public static void deleteFiles(String[] files) {
        for (String file : files) {
            try {
                File newFile = new File(file);
                newFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            Log.i("filter", item.title + ": " + item.status.toString());
            predicate = filter.hasFlags(item.status.getValue());
            if (predicate) {
                result.add(item);
            }
        }
        return (result);
    }

    public static boolean copyStreamToFile(InputStream input, File target) {
        try {
            target.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(target);
            return copyStreamToStream(input, output);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyStreamToStream(InputStream input, OutputStream output) {
        boolean result = false;
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
