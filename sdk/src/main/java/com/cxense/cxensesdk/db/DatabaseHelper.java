package com.cxense.cxensesdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for working with database
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracks.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static void parseCursor(Cursor cursor, ContentValues values) {
        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_NULL:
                    values.putNull(columns[i]);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    values.put(columns[i], cursor.getLong(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    values.put(columns[i], cursor.getDouble(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    values.put(columns[i], cursor.getString(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    values.put(columns[i], cursor.getBlob(i));
                    break;
            }
        }
    }

    static ContentValues cursorRowToContentValues(Cursor cursor) {
        ContentValues values = new ContentValues();
        parseCursor(cursor, values);
        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < DATABASE_VERSION; i++)
            migrate(db, i);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++)
            migrate(db, i);
    }

    public long save(DatabaseObject databaseObject) {
        ContentValues values = databaseObject.toContentValues();
        SQLiteDatabase db = getWritableDatabase();
        Integer id = databaseObject.id;
        String tableName = databaseObject.getTableName();
        if (id == null)
            return db.insert(tableName, null, values);
        return db.update(tableName, values, DatabaseObject._ID + "= ?", new String[]{"" + id});
    }

    public int delete(DatabaseObject databaseObject) {
        Integer id = databaseObject.id;
        if (id == null)
            return -1;
        return delete(databaseObject.getTableName(), DatabaseObject._ID + "= ?", new String[]{"" + id});
    }

    public int delete(String tableName, String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(tableName, whereClause, whereArgs);
    }

    public List<ContentValues> query(String tableName, String[] columns, String selection, String[] selectionArgs,
                                     String groupBy, String having, String orderBy) {
        return query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, null);
    }

    public List<ContentValues> query(String tableName, String[] columns, String selection, String[] selectionArgs,
                                     String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = null;
        List<ContentValues> values = new ArrayList<>();
        try {
            cursor = getReadableDatabase().query(tableName, columns, selection, selectionArgs,
                    groupBy, having, orderBy, limit);
            while (cursor.moveToNext()) {
                values.add(cursorRowToContentValues(cursor));
            }
            return values;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Migrates database from {@code oldVersion} to {@code oldVersion + 1}
     *
     * @param db         database
     * @param oldVersion current database version
     */
    protected void migrate(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 0:
                // database does not exist, create it
                db.execSQL("CREATE TABLE " + EventRecord.TABLE_NAME + " ("
                        + EventRecord._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EventRecord.EVENT_CUSTOM_ID + " TEXT,"
                        + EventRecord.EVENT + " TEXT,"
                        + EventRecord.TIME + " INTEGER,"
                        + EventRecord.CKP + " TEXT,"
                        + EventRecord.RND + " TEXT,"
                        + EventRecord.EVENT_TYPE + " TEXT,"
                        + EventRecord.SPENT_TIME + " INTEGER,"
                        + EventRecord.IS_SENT + " INTEGER);");
                break;
        }
    }
}
