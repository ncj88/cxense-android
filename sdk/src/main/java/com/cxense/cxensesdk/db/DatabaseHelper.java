package com.cxense.cxensesdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cxense.db.AbstractDatabaseHelper;

/**
 * Class for working with database.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class DatabaseHelper extends AbstractDatabaseHelper {
    static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracks.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    /**
     * Migrates database from {@code oldVersion} to {@code oldVersion + 1}
     *
     * @param db         database
     * @param oldVersion current database version
     */
    @Override
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
