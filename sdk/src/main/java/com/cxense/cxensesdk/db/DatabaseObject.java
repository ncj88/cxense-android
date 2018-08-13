package com.cxense.cxensesdk.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

/**
 * Base class for database objects.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public abstract class DatabaseObject implements BaseColumns {
    public static final String[] COLUMNS = {_ID};
    /**
     * Contains primary key
     */
    public Integer id;

    protected DatabaseObject() {
    }

    protected DatabaseObject(ContentValues values) {
        this();
        id = values.getAsInteger(_ID);
    }

    public abstract String getTableName();

    protected ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(_ID, id);
        return values;
    }
}
