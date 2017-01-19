package fr.todolist.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLite extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Todo";

    public static final String COL_ID = "ID";
    public static final int NUM_COL_ID = 0;

    public static final String COL_TITLE = "title";
    public static final int NUM_COL_TITLE = 1;

    public static final String COL_CONTENT = "content";
    public static final int NUM_COL_CONTENT = 2;

    public static final String COL_DUE_DATE = "due_date";
    public static final int NUM_COL_DUE_DATE = 3;

    public static final String COL_STATUS = "status";
    public static final int NUM_COL_STATUS = 4;

    private static final String TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITLE + " TEXT NOT NULL, "
            + COL_CONTENT + " TEXT NOT NULL, "
            + COL_DUE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + COL_STATUS + " TEXT NOT NULL);";

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
        db.execSQL(TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME + ";");
        onCreate(db);
    }

}
