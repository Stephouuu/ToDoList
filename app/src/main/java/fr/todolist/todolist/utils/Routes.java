package fr.todolist.todolist.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stephane on 18/11/2016.
 */

/**
 * Manage inApp path
 */
public class Routes {

    private static final String CONSULTATION = "consultation";

    private static final List<String> List = new ArrayList<>();

    /**
     * Load the input scheme
     * @param uri Scheme
     */
    public static void Load(Uri uri) {
        if (uri != null) {
            String host = uri.getHost();
            String path = uri.getPath();

            String[] array = path.split("/");

            Log.i("routes", "'" + host + "'");
            addRoute(host);
            for (String item : array) {
                if (item.length() > 0) {
                    Log.i("routes", "'" + item + "'");
                    addRoute(item);
                }
            }
        }
    }

    public static void Clear() {
        List.clear();
    }

    @Nullable
    public static String GetConsultationID() {
        String ID = null;

        if (List.contains(CONSULTATION)) {
            int index = List.indexOf(CONSULTATION);
            ID = List.get(index + 1);
            removeRange(index, index + 1);
        }
        return (ID);
    }

    /**
     * Add a path
     * @param param The path
     */
    private static void addRoute(String param) {
        List.add(param);
    }

    private static void removeRange(int min, int max) {
        for (int i = min ; i <= max ; ++i) {
            Log.i("routes", "removing " + List.get(min));
            List.remove(min);
        }
    }

    private static String[] getRange(int min, int max) {
        String[] array = new String[max - min + 1];
        for (int i = min ; i <= max ; ++i) {
            array[i - min] = List.get(i);
        }
        return (array);
    }

}
