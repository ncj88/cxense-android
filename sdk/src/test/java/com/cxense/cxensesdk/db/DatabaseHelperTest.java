package com.cxense.cxensesdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-04).
 */
@RunWith(PowerMockRunner.class)
public class DatabaseHelperTest {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ContentValues values;
    private DatabaseObject databaseObject;
    private Cursor cursor;

    @Before
    public void setUp() throws Exception {
        Context context = mock(Context.class);
        db = mock(SQLiteDatabase.class);
        values = mock(ContentValues.class);
        databaseObject = mock(DatabaseObject.class);
        cursor = mock(Cursor.class);
        databaseHelper = spy(new DatabaseHelper(context));
        doReturn(db).when(databaseHelper).getWritableDatabase();
        doReturn(db).when(databaseHelper).getReadableDatabase();
        doReturn(values).when(databaseObject).toContentValues();
        doReturn(false).when(cursor).moveToNext();
        doNothing().when(values).putNull(anyString());
        doReturn(1L).when(db).insert(any(), any(), any(ContentValues.class));
        doReturn(1).when(db).update(any(), any(ContentValues.class), any(), any(String[].class));
        doReturn(1).when(db).delete(any(), any(), any());
        doReturn(cursor).when(db).query(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void parseCursor() throws Exception {
        int[] types = new int[]{
                Cursor.FIELD_TYPE_NULL, Cursor.FIELD_TYPE_INTEGER, Cursor.FIELD_TYPE_FLOAT,
                Cursor.FIELD_TYPE_STRING, Cursor.FIELD_TYPE_BLOB
        };
        String[] columns = Arrays.stream(types).mapToObj(String::valueOf).toArray(String[]::new);
        when(cursor.getColumnNames()).thenReturn(columns);
        when(cursor.getType(anyInt())).thenReturn(Cursor.FIELD_TYPE_NULL);
        when(cursor.getType(anyInt())).thenAnswer(invocation -> {
            int arg = invocation.getArgument(0);
            if (arg < types.length)
                return types[arg];
            return types[0];
        });
        DatabaseHelper.parseCursor(cursor, values);
        verify(values).putNull(anyString());
        verify(values).put(anyString(), anyLong());
        verify(values).put(anyString(), anyDouble());
        verify(values).put(anyString(), nullable(String.class));
        verify(values).put(anyString(), nullable(byte[].class));
    }

    @Test
    public void cursorRowToContentValues() throws Exception {
        when(cursor.getColumnNames()).thenReturn(new String[]{});
        DatabaseHelper.cursorRowToContentValues(cursor);
    }

    @Test
    public void onCreate() throws Exception {
        doNothing().when(databaseHelper).migrate(any(), anyInt());
        databaseHelper.onCreate(db);
        verify(databaseHelper, times(DatabaseHelper.DATABASE_VERSION)).migrate(eq(db), anyInt());
    }

    @Test
    public void onUpgrade() throws Exception {
        int oldVersion = 3, newVersion = 5;
        doNothing().when(databaseHelper).migrate(any(), anyInt());
        databaseHelper.onUpgrade(db, oldVersion, newVersion);
        verify(databaseHelper, times(newVersion - oldVersion))
                .migrate(eq(db), intThat(argument -> argument >= oldVersion && argument < newVersion));
    }

    @Test
    public void save() throws Exception {
        databaseObject.id = 1;
        assertEquals(1, databaseHelper.save(databaseObject));
    }

    @Test
    public void saveNewObject() throws Exception {
        databaseObject.id = null;
        assertEquals(1L, databaseHelper.save(databaseObject));
    }

    @Test
    public void delete() throws Exception {
        databaseObject.id = 1;
        assertEquals(1, databaseHelper.delete(databaseObject));
    }

    @Test
    public void deleteNotExistObject() throws Exception {
        databaseObject.id = null;
        assertEquals(-1, databaseHelper.delete(databaseObject));
    }

    @Test
    public void deleteFullArgs() throws Exception {
        assertEquals(1, databaseHelper.delete("table", null, null));
    }

    @Test
    public void query() throws Exception {
        assertNotNull(databaseHelper.query("table", new String[]{}, null,
                null, null, null, null));
    }

    @Test
    public void queryFullArgs() throws Exception {
        assertNotNull(databaseHelper.query("table", new String[]{}, null,
                null, null, null, null, null));
    }

    @Test
    public void migrate() throws Exception {
        for (int i = 0; i < DatabaseHelper.DATABASE_VERSION; i++) {
            databaseHelper.migrate(db, i);
            verify(db).execSQL(startsWith("CREATE TABLE " + EventRecord.TABLE_NAME));
        }
    }

}