package fr.todolist.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLite extends SQLiteOpenHelper {

    public static final String TODO_TABLE_NAME = "Todo";

    public static final String TODO_COL_ID = "ID";
    public static final int TODO_NUM_COL_ID = 0;

    public static final String TODO_COL_TITLE = "title";
    public static final int TODO_NUM_COL_TITLE = 1;

    public static final String TODO_COL_CONTENT = "content";
    public static final int TODO_NUM_COL_CONTENT = 2;

    public static final String TODO_COL_DUE_DATE = "due_date";
    public static final int TODO_NUM_COL_DUE_DATE = 3;

    public static final String TODO_COL_FLAG_STATUS = "status";
    public static final int TODO_NUM_COL_FLAG_STATUS = 4;

    public static final String TODO_COL_REMIND = "remind";
    public static final int TODO_NUM_COL_REMIND = 5;

    public static final String TODO_COL_NB_RECURRENCE = "nbRecurrence";
    public static final int TODO_NUM_COL_NB_RECURRENCE = 6;

    public static final String TODO_COL_INTERVAL = "interval";
    public static final int TODO_NUM_COL_INTERVAL = 7;

    public static final String TODO_COL_BASE_NB_RECURRENCE = "nbBaseRecurrence";
    public static final int TODO_NUM_COL_BASE_NB_RECURRENCE = 8;

    public static final String TODO_COL_PRIORITY = "priority";
    public static final int TODO_NUM_COL_PRIORITY = 9;

    private static final String TODO_TABLE = "CREATE TABLE " + TODO_TABLE_NAME + " ("
            + TODO_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TODO_COL_TITLE + " TEXT NOT NULL, "
            + TODO_COL_CONTENT + " TEXT NOT NULL, "
            + TODO_COL_DUE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + TODO_COL_FLAG_STATUS + " INTEGER DEFAULT 0, "
            + TODO_COL_REMIND + " INTEGER DEFAULT 0, "
            + TODO_COL_NB_RECURRENCE + " INTEGER DEFAULT 0, "
            + TODO_COL_INTERVAL + " INTEGER DEFAULT 0, "
            + TODO_COL_BASE_NB_RECURRENCE + " INTEGER DEFAULT 0, "
            + TODO_COL_PRIORITY + " INTEGER DEFAULT 0);";


    public static final String ALARM_TABLE_NAME = "Alarm";

    public static final String ALARM_COL_ID = "ID";
    public static final int ALARM_NUM_COL_ID = 0;

    public static final String ALARM_COL_ID_ITEM = "id_item";
    public static final int ALARM_NUM_COL_ID_ITEM = 1;

    public static final String ALARM_COL_TITLE = "title";
    public static final int ALARM_NUM_COL_TITLE = 2;

    public static final String ALARM_COL_CONTENT = "content";
    public static final int ALARM_NUM_COL_CONTENT = 3;

    private static final String ALARM_TABLE = "CREATE TABLE " + ALARM_TABLE_NAME + " ("
            + ALARM_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ALARM_COL_ID_ITEM + " INTEGER NOT NULL, "
            + ALARM_COL_TITLE + " TEXT NOT NULL, "
            + ALARM_COL_CONTENT + " TEXT NOT NULL);";

    /**
     * @param context Le contexte
     * @param name Le nom du fichier de la BDD
     * @param factory Personnalisation de la classe Cursor
     * @param version La version de la base de donnée
     */
    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO_TABLE);
        db.execSQL(ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TODO_TABLE_NAME + ";");
        db.execSQL("DROP TABLE " + ALARM_TABLE_NAME + ";");
        onCreate(db);
    }

}
