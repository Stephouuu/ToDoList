package fr.todolist.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public class TodoItemDatabase implements SearchInterface {

    private Context context;

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
        this.context = context;
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

    public TodoItemInfo insertItem(TodoItemInfo item) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.COL_TITLE, item.title);
        values.put(MySQLite.COL_CONTENT, item.content);
        values.put(MySQLite.COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.COL_FLAG_STATUS, item.status.getValue());

        item.id = database.insert(MySQLite.TABLE_NAME, null, values);
        return (item);
    }

    public int updateTodoItem(TodoItemInfo item) {
        item.dateTime = DateTimeManager.formatDateTime(item.year, item.month, item.day, item.hour, item.minute);

        ContentValues values = new ContentValues();
        values.put(MySQLite.COL_TITLE, item.title);
        values.put(MySQLite.COL_CONTENT, item.content);
        values.put(MySQLite.COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.COL_FLAG_STATUS, item.status.getValue());

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

    @Override
    public TodoItemFilter getFilter() {
        return null;
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateASC() {
        return (getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " ORDER BY " + MySQLite.COL_DUE_DATE + ";"));
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateDESC() {
        return (getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " ORDER BY " + MySQLite.COL_DUE_DATE + " DESC;"));
    }

    @Override
    public List<TodoItemInfo> getItemsByTitle(String toSearch) {
        return (getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_TITLE + " LIKE '" + toSearch + "%'"));
    }

    @Override
    public List<TodoItemInfo> getItemsByStatus(TodoItemInfo.Status toSearch) {
        return (getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_FLAG_STATUS + " = " + toSearch.getValue() + ";"));
    }

    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch) {
        return getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_CONTENT + " LIKE '%" + toSearch + "%'");
    }

    @Nullable
    public TodoItemInfo getItemByID(int id) {
        List<TodoItemInfo> list = getResult("SELECT * FROM " + MySQLite.TABLE_NAME + " WHERE " + MySQLite.COL_ID + " ='" + id + "';");
        return (list.get(0));
    }

    private List<TodoItemInfo> getResult(String query) {
        List<TodoItemInfo> results = new ArrayList<>();

        Cursor cursor = database.rawQuery(query, null);

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

        int status = cursor.getInt(MySQLite.NUM_COL_FLAG_STATUS);
        if (status == TodoItemInfo.Status.ToDo.getValue()) {
            item.status = TodoItemInfo.Status.ToDo;
        } else if (status == TodoItemInfo.Status.Done.getValue()) {
            item.status = TodoItemInfo.Status.Done;
        } else if (status == TodoItemInfo.Status.Expired.getValue()) {
            item.status = TodoItemInfo.Status.Expired;
        }

        DateTimeManager.retrieveDateTime(item, item.dateTime);

        return (item);
    }
}
