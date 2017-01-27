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
import fr.todolist.todolist.utils.SortingInfo;
import fr.todolist.todolist.utils.TodoItemFilter;
import fr.todolist.todolist.utils.TodoItemInfo;

/**
 * Created by Stephane on 16/01/2017.
 */


/**
 * Manage the application database
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
    public static final String BDD_NAME = "todo.db";

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
        mySQLite = new MySQLite(context, BDD_NAME, null, VERSION_BDD);
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

    /**
     * Insert a To doItemInfo
     * @param item The item
     * @return The item
     */
    public TodoItemInfo insertItem(TodoItemInfo item) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.TODO_COL_TITLE, item.title);
        values.put(MySQLite.TODO_COL_CONTENT, item.content);
        values.put(MySQLite.TODO_COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.TODO_COL_FLAG_STATUS, item.status.getValue());
        values.put(MySQLite.TODO_COL_REMIND, item.remind?1:0);
        values.put(MySQLite.TODO_COL_NB_RECURRENCE, item.nbRecurrence);
        values.put(MySQLite.TODO_COL_INTERVAL, item.intervalRecurrence);
        values.put(MySQLite.TODO_COL_INTERVAL_TYPE, item.intervalType);
        values.put(MySQLite.TODO_COL_BASE_NB_RECURRENCE, item.nbBaseRecurrence);
        values.put(MySQLite.TODO_COL_PRIORITY, item.priority);
        values.put(MySQLite.TODO_COL_ILLUSTRATIONS, item.photos);

        item.id = database.insert(MySQLite.TODO_TABLE_NAME, null, values);
        return (item);
    }


    /**
     * Insert a AlertInfo in the database
     * @param alert The alert
     * @return The alert
     */
    public AlertInfo insertItem(AlertInfo alert) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.ALARM_COL_ID_ITEM, alert.idTodoItem);
        values.put(MySQLite.ALARM_COL_TITLE, alert.title);
        values.put(MySQLite.ALARM_COL_CONTENT, alert.content);

        alert.id = (int)database.insert(MySQLite.ALARM_TABLE_NAME, null, values);
        return (alert);
    }

    /**
     * Update a To doItemInfo in the database
     * @param item The item
     * @return The number of row reached
     */
    public int updateItem(TodoItemInfo item) {
        item.dateTime = DateTimeManager.formatDateTime(item.year, item.month, item.day, item.hour, item.minute);

        ContentValues values = new ContentValues();
        values.put(MySQLite.TODO_COL_TITLE, item.title);
        values.put(MySQLite.TODO_COL_CONTENT, item.content);
        values.put(MySQLite.TODO_COL_DUE_DATE, item.dateTime);
        values.put(MySQLite.TODO_COL_FLAG_STATUS, item.status.getValue());
        values.put(MySQLite.TODO_COL_REMIND, item.remind?1:0);
        values.put(MySQLite.TODO_COL_NB_RECURRENCE, item.nbRecurrence);
        values.put(MySQLite.TODO_COL_INTERVAL, item.intervalRecurrence);
        values.put(MySQLite.TODO_COL_INTERVAL_TYPE, item.intervalType);
        values.put(MySQLite.TODO_COL_BASE_NB_RECURRENCE, item.nbBaseRecurrence);
        values.put(MySQLite.TODO_COL_PRIORITY, item.priority);
        values.put(MySQLite.TODO_COL_ILLUSTRATIONS, item.photos);

        return (database.update(MySQLite.TODO_TABLE_NAME, values, MySQLite.TODO_COL_ID + " = " + item.id, null));
    }

    public int updateItem(AlertInfo alert) {
        ContentValues values = new ContentValues();
        values.put(MySQLite.ALARM_COL_ID_ITEM, alert.idTodoItem);
        values.put(MySQLite.ALARM_COL_TITLE, alert.title);
        values.put(MySQLite.ALARM_COL_CONTENT, alert.content);

        return (database.update(MySQLite.ALARM_TABLE_NAME, values, MySQLite.ALARM_COL_ID + " = " + alert.id, null));
    }

    /**
     * Return the AlertInfo corresponding to the ID
     * @param id The ID to get
     * @return The AlertInfo corresponding to the ID
     */
    public AlertInfo getAlertInfoByItemID(int id) {
        List<AlertInfo> result = getAlertResult("SELECT * FROM " + MySQLite.ALARM_TABLE_NAME + " WHERE " + MySQLite.ALARM_COL_ID_ITEM + " = " + id + ";");
        if (result.size() > 0) {
            return result.get(0);
        }
        return (null);
    }

    /**
     * Get the alerts
     * @return All Alerts in the Database
     */
    public List<AlertInfo> getAlerts() {
        return (getAlertResult("SELECT * FROM " + MySQLite.ALARM_TABLE_NAME));
    }

    /**
     * Delete a to doItemInfo corresponding to the ID
     * @param id The id of the item to delete
     * @return Number of row reached
     */
    public int deleteItem(long id) {
        return (database.delete(MySQLite.TODO_TABLE_NAME, MySQLite.TODO_COL_ID + " = "  + id, null));
    }

    /**
     * Delete a AlertInfo corresponding to the ID
     * @param id id The id of the alert to delete
     * @return Number of row reached
     */
    public int deleteAlert(int id) {
        return (database.delete(MySQLite.ALARM_TABLE_NAME, MySQLite.ALARM_COL_ID + " = " + id, null));
    }

    /**
     * Return the Filter
     * @return NULL
     */
    @Override
    public TodoItemFilter getFilter() {
        return null;
    }

    /**
     * Return the sorting Information
     * @return NULL
     */
    @Override
    public SortingInfo getSortingInfo() {
        return (null);
    }

    /**
     * Return a To doItemInfo list order by date
     * @param date The order
     * @return The list of item found
     */
    @Override
    public List<TodoItemInfo> getItemsByDueDate(SortingInfo.Type date) {
        String orderDate = (date == SortingInfo.Type.Ascendant) ? "ASC" : "DESC";
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " ORDER BY " + MySQLite.TODO_COL_FLAG_STATUS
                + " DESC, " + MySQLite.TODO_COL_DUE_DATE + " " + orderDate + ";"));
    }

    /**
     * Return a To doItemInfo list corresponding to the title
     * @param toSearch The title to search
     * @param date The order
     * @return The list of item found
     */
    @Override
    public List<TodoItemInfo> getItemsByTitle(String toSearch, SortingInfo.Type date) {
        String orderDate = (date == SortingInfo.Type.Ascendant) ? "ASC" : "DESC";
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_TITLE + " LIKE '%" + toSearch + "%'"
                + " ORDER BY " + MySQLite.TODO_COL_FLAG_STATUS + " DESC, " + MySQLite.TODO_COL_DUE_DATE + " " + orderDate + ";"));
    }

    /**
     * Return a To doItemInfo list corresponding of the content
     * @param toSearch The content to search
     * @param date The order
     * @return The list of item found
     */
    @Override
    public List<TodoItemInfo> getItemsByContent(String toSearch, SortingInfo.Type date) {
        String orderDate = (date == SortingInfo.Type.Ascendant) ? "ASC" : "DESC";
        return (getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_CONTENT + " LIKE '%" + toSearch + "%'"
                + " ORDER BY " + MySQLite.TODO_COL_FLAG_STATUS + " DESC,"+ MySQLite.TODO_COL_DUE_DATE + " " + orderDate + ";"));
    }

    /**
     * Get a To doItemInfo by the ID
     * @param id The ID
     * @return The item found
     */
    @Nullable
    public TodoItemInfo getItemByID(int id) {
        List<TodoItemInfo> list = getTodoItemResult("SELECT * FROM " + MySQLite.TODO_TABLE_NAME + " WHERE " + MySQLite.TODO_COL_ID + " ='" + id + "';");
        return ((list.size() > 0) ? list.get(0) : null);
    }

    /**
     * Get a Alert by the ID
     * @param id The ID
     * @return The item found
     */
    @Nullable
    public AlertInfo getAlertByID(int id) {
        List<AlertInfo> list = getAlertResult("SELECT * FROM " + MySQLite.ALARM_TABLE_NAME + " WHERE " + MySQLite.ALARM_COL_ID + " ='" + id + "';");
        return ((list.size() > 0) ? list.get(0) : null);
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
        item.nbRecurrence = cursor.getInt(MySQLite.TODO_NUM_COL_NB_RECURRENCE);
        item.intervalRecurrence = cursor.getLong(MySQLite.TODO_NUM_COL_INTERVAL);
        item.intervalType = cursor.getString(MySQLite.TODO_NUM_COL_INTERVAL_TYPE);
        item.nbBaseRecurrence = cursor.getInt(MySQLite.TODO_NUM_COL_BASE_NB_RECURRENCE);
        item.priority = cursor.getInt(MySQLite.TODO_NUM_COL_PRIORITY);
        item.photos = cursor.getString(MySQLite.TODO_NUM_COL_ILLUSTRATIONS);

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
