package fr.todolist.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.utils.StaticTools;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoItemDatabase {

    /**
     * Version de la base de donnée
     */
    private static final int VERSION_BDD = 1;

    /**
     * Nom du fichier de la base de donnée
     */
    private static final String NOM_BDD = "todo.db";

    /**
     * La base de donnée SQLite
     */
    private SQLiteDatabase database;

    /**
     * Notre classe permettant de créer plus facilement une BDD
     */
    private MySQLite mySQLite;


    /**
     * Crée une base de donnée
     *
     * @param context Le contexte
     */
    public TodoItemDatabase(Context context) {
        mySQLite = new MySQLite(context, NOM_BDD, null, VERSION_BDD);

    }

    /**
     * Ouvre la base de donnée en écriture
     */
    public void open() {
        database = mySQLite.getWritableDatabase();
    }

    /**
     * Ferme la base de donnée
     */
    public void close() {
        database.close();
    }

    public long insertItem(TodoItemInfo item) {

        ContentValues values = new ContentValues();
        values.put(MySQLite.COL_TITLE, item.title);
        values.put(MySQLite.COL_CONTENT, item.content);
        values.put(MySQLite.COL_DUE_DATE, StaticTools.formatDateTime(item.year, item.month, item.day,
                                            item.hour, item.minute));

        return (database.insert(MySQLite.TABLE_NAME, null, values));
    }

    public int updateElephant(TodoItemInfo item) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.COL_TITLE, item.title);
        values.put(MySQLite.COL_CONTENT, item.content);
        values.put(MySQLite.COL_DUE_DATE, StaticTools.formatDateTime(item.year, item.month, item.day,
                item.hour, item.minute));

        return (database.update(MySQLite.TABLE_NAME, values, MySQLite.COL_ID + " = " + item.id, null));
    }

    /**
     * Supprime un éléphant de la base de donnée
     * @param id L'ID de l'éléphant à supprimer
     * @return Code d'erreur
     */
    public int deleteItem(int id) {
        return (database.delete(MySQLite.TABLE_NAME, MySQLite.COL_ID + " = "  + id, null));
    }

    public List<TodoItemInfo> getItems() {
        String request = "SELECT * FROM " + MySQLite.TABLE_NAME;
        request += " ORDER BY " + MySQLite.COL_DUE_DATE;
        List<TodoItemInfo> results = new ArrayList<>();

        Cursor cursor = database.rawQuery(request, null);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToTodoItem(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return (results);
    }

    /*public List<TodoItemInfo> getElephantByName(String name) {
        String request = "SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_NAME + " LIKE '" + name + "%'";
        List<ElephantInfo> results = new ArrayList<>();

        Cursor cursor = database.rawQuery(request, null);
        //Cursor cursor = database.rawQuery(request, new String[] {name});

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToElephant(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return (results);
    }*/

    /*public List<ElephantInfo> getElephant(String name, ElephantInfo.Gender sex) {
        String request = "SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_NAME + " LIKE '" + name + "%'";
        List<ElephantInfo> results = new ArrayList<>();

        if (sex != ElephantInfo.Gender.UNKNOWN) {
            request += " AND " + MySQLite.COL_SEX + " = '" + String.valueOf(sex) + "';";
        } else {
            request += ";";
        }

        Log.i("request", request);

        Cursor cursor = database.rawQuery(request, null);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToElephant(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return (results);
    }

    //TODO: rename function
    public List<ElephantInfo> getCustom(ElephantInfo info) {
        String query = "SELECT * FROM " + MySQLite.TABLE_NAME + GetElephantQueryRestriction(info);
        List<ElephantInfo> results = new ArrayList<>();
        Log.i("request", query);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToElephant(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return (results);
    }*/

    /*private String GetElephantQueryRestriction(ElephantInfo info) {
        List<String> param = new ArrayList<String>();
        String restriction = " ";

        if (!info.name.isEmpty())
            param.add("WHERE " + MySQLite.COL_NAME + " = '" + info.name + "'");
        if (!info.chips1.isEmpty())
            param.add("WHERE " + MySQLite.COL_CHIPS + " = '" + info.chips1 + "'");
        if (info.sex != ElephantInfo.Gender.UNKNOWN)
            param.add("WHERE " + MySQLite.COL_SEX + " = '" + info.sex + "'");
        if (!info.registrationProvince.isEmpty())
            param.add("WHERE " + MySQLite.COL_REGISTRATION_PROVINCE + " = '" + info.registrationProvince + "'");
        if (!info.registrationDistrict.isEmpty())
            param.add("WHERE " + MySQLite.COL_REGISTRATION_DISTRICT + " = '" + info.registrationDistrict + "'");
        if (!info.registrationProvince.isEmpty())
            param.add("WHERE " + MySQLite.COL_REGISTRATION_PROVINCE + " = '" + info.registrationProvince + "'");
        if (!info.registrationVillage.isEmpty())
            param.add("WHERE " + MySQLite.COL_REGISTRATION_VILLAGE + " = '" + info.registrationVillage + "'");

        for (int i = 0;  i < param.size(); i++) {
            if (i > 0)
                param.set(i- 1,  param.get(i - 1) + " AND ");
        }

        for (int i = 0; i < param.size(); i++) {
            restriction += param.get(i);
        }

        return restriction;
    }*/

    /**
     * Récuperer les infos d'un cursor et les convertis en Elefant
     *
     * @param cursor Le curseur
     * @return L'éléphant
     */
    private TodoItemInfo cursorToTodoItem(Cursor cursor){
        TodoItemInfo item = new TodoItemInfo();
        item.id = cursor.getInt(MySQLite.NUM_COL_ID);
        item.title = cursor.getString(MySQLite.NUM_COL_TITLE);
        item.content = cursor.getString(MySQLite.NUM_COL_CONTENT);
        item.dateTime = cursor.getString(MySQLite.NUM_COL_DUE_DATE);

        StaticTools.retrieveDateTime(item, item.dateTime);

        return (item);
    }
}
