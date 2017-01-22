package fr.todolist.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.todolist.todolist.interfaces.SearchInterface;
import fr.todolist.todolist.utils.AlertInfo;
import fr.todolist.todolist.utils.DateTimeManager;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */

public class AppDatabase implements SearchInterface {

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
    public AppDatabase(Context context) {
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
        values.put(MySQLite.TODO_COL_TITLE, item.title);
        values.put(MySQLite.TODO_COL_CONTENT, item.content);
        values.put(MySQLite.TODO_COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.TODO_COL_FLAG_STATUS, item.status.getValue());
        values.put(MySQLite.TODO_COL_REMIND, item.remind?1:0);

        item.id = database.insert(MySQLite.TODO_TABLE_NAME, null, values);
        return (item);
    }

    public AlertInfo insertItem(AlertInfo alert) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.ALARM_COL_ID_ITEM, alert.idTodoItem);
        values.put(MySQLite.ALARM_COL_TITLE, alert.title);
        values.put(MySQLite.ALARM_COL_CONTENT, alert.content);

        alert.id = (int)database.insert(MySQLite.ALARM_TABLE_NAME, null, values);
        return (alert);
    }

    public int updateItem(TodoItemInfo item) {
        item.dateTime = DateTimeManager.formatDateTime(item.year, item.month, item.day, item.hour, item.minute);

        ContentValues values = new ContentValues();
        values.put(MySQLite.TODO_COL_TITLE, item.title);
        values.put(MySQLite.TODO_COL_CONTENT, item.content);
        values.put(MySQLite.TODO_COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.TODO_COL_FLAG_STATUS, item.status.getValue());
        values.put(MySQLite.TODO_COL_REMIND, item.remind?1:0);

        return (database.update(MySQLite.TODO_TABLE_NAME, values, MySQLite.TODO_COL_ID + " = " + item.id, null));
    }

    public int updateItem(AlertInfo alert) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.ALARM_COL_ID_ITEM, alert.idTodoItem);
        values.put(MySQLite.ALARM_COL_TITLE, alert.title);
        values.put(MySQLite.ALARM_COL_CONTENT, alert.content);

        return (database.update(MySQLite.TODO_TABLE_NAME, values, MySQLite.TODO_COL_ID + " = " + alert.id, null));
    }

    /*public AlertInfo getAlertInfoByID(int id) {
        String[] what = new String[] {MySQLite.ALARM_COL_ID, MySQLite.ALARM_COL_TITLE, MySQLite.ALARM_COL_CONTENT};
        String where = MySQLite.ALARM_COL_ID + " = " + id + ";";

        return (cursorToAlertInfo(database.query(MySQLite.ALARM_TABLE_NAME, what, where, null, null, null, null)));
    }*/

    public AlertInfo getAlertInfoByItemID(int id) {
        List<AlertInfo> result = getAlertResult("SELECT * FROM " + MySQLite.ALARM_TABLE_NAME + " WHERE " + MySQLite.ALARM_COL_ID_ITEM + " = " + id + ";");
        if (result.size() > 0) {
            return result.get(0);
        }
        return (null);
    }

    public List<AlertInfo> getAlerts() {
        return (getAlertResult("SELECT * FROM " + MySQLite.ALARM_TABLE_NAME));
    }

    public int deleteItem(long id) {
        return (database.delete(MySQLite.TODO_TABLE_NAME, MySQLite.TODO_COL_ID + " = "  + id, null));
    }

    public int deleteAlert(int id) {
        return (database.delete(MySQLite.ALARM_TABLE_NAME, MySQLite.ALARM_COL_ID + " = " + id, null));
    }

    @Override
    public TodoItemFilter getFilter() {
        return null;
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateASC() {
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " ORDER BY " + MySQLite.TODO_COL_DUE_DATE + ";"));
    }

    @Override
    public List<TodoItemInfo> getItemsByDueDateDESC() {
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " ORDER BY " + MySQLite.TODO_COL_DUE_DATE + " DESC;"));
    }

    @Override
    public List<TodoItemInfo> getItemsByTitle(String toSearch) {
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_TITLE + " LIKE '" + toSearch + "%'"));
    }

    @Override
    public List<TodoItemInfo> getItemsByStatus(TodoItemInfo.Status toSearch) {
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_FLAG_STATUS + " = " + toSearch.getValue() + ";"));
    }

    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch) {
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_CONTENT + " LIKE '%" + toSearch + "%'"));
    }

    @Nullable
    public TodoItemInfo getItemByID(int id) {
        List<TodoItemInfo> list = getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_ID + " ='" + id + "';");
        return (list.get(0));
    }

    private List<TodoItemInfo> getTodoItemResult(String query) {
        List<TodoItemInfo> results = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToTodoItemInfo(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return (results);
    }

    private List<AlertInfo> getAlertResult(String query) {
        List<AlertInfo> results = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToAlertInfo(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return (results);
    }

    /**
     * Récuperer les infos d'un cursor et les convertis en TodoItemInfo
     *
     * @param cursor Le curseur
     * @return L'item
     */
    private TodoItemInfo cursorToTodoItemInfo(Cursor cursor) {
        TodoItemInfo item = new TodoItemInfo();
        item.id = cursor.getInt(MySQLite.TODO_NUM_COL_ID);
        item.title = cursor.getString(MySQLite.TODO_NUM_COL_TITLE);
        item.content = cursor.getString(MySQLite.TODO_NUM_COL_CONTENT);
        item.dateTime = cursor.getString(MySQLite.TODO_NUM_COL_DUE_DATE);

        int status = cursor.getInt(MySQLite.TODO_NUM_COL_FLAG_STATUS);
        if (status == TodoItemInfo.Status.ToDo.getValue()) {
            item.status = TodoItemInfo.Status.ToDo;
        } else if (status == TodoItemInfo.Status.Done.getValue()) {
            item.status = TodoItemInfo.Status.Done;
        } else if (status == TodoItemInfo.Status.Overdue.getValue()) {
            item.status = TodoItemInfo.Status.Overdue;
        }

        item = DateTimeManager.retrieveDateTime(item, item.dateTime);

        item.remind = cursor.getInt(MySQLite.TODO_NUM_COL_REMIND) == 1;

        return (item);
    }

    private AlertInfo cursorToAlertInfo(Cursor cursor) {
        AlertInfo alert = new AlertInfo();
        alert.id = cursor.getInt(MySQLite.ALARM_NUM_COL_ID);
        alert.idTodoItem = cursor.getInt(MySQLite.ALARM_NUM_COL_ID_ITEM);
        alert.title = cursor.getString(MySQLite.ALARM_NUM_COL_TITLE);
        alert.content = cursor.getString(MySQLite.ALARM_NUM_COL_CONTENT);

        return (alert);
    }
}
