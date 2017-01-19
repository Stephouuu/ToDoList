package fr.todolist.todolist.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stephane on 18/11/2016.
 */

/**
 * Cette classe a pour but d'handle les routes in-App
 */
public class Routes {

    private static final String CONSULTATION = "consultation";

    private static final List<String> List = new ArrayList<>();

    /**
     * Charge le scheme reçu
     * @param uri Le scheme
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
    public static String GetConsultationID(@NonNull Context context) {
        String ID = null;

        if (List.contains(CONSULTATION)) {
            int index = List.indexOf(CONSULTATION);
            ID = List.get(index + 1);
            removeRange(index, index + 1);
        }
        return (ID);
    }

    /**
     * Ajoute une route
     * @param param La route
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
