package com.cxense.cxensesdk.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    @Nullable
    public Integer id;

    protected DatabaseObject() {
    }

    protected DatabaseObject(@NonNull ContentValues values) {
        this();
        id = values.getAsInteger(_ID);
    }

    @NonNull
    public abstract String getTableName();

    @NonNull
    protected ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(_ID, id);
        return values;
    }
}
