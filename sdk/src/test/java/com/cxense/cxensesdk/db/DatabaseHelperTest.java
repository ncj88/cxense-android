package com.cxense.cxensesdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-04).
 */
@RunWith(PowerMockRunner.class)
public class DatabaseHelperTest {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() throws Exception {
        Context context = mock(Context.class);
        databaseHelper = new DatabaseHelper(context);
        db = mock(SQLiteDatabase.class);
    }

    @Test
    public void migrate() throws Exception {
        for (int i = 0; i < DatabaseHelper.DATABASE_VERSION; i++) {
            databaseHelper.migrate(db, i);
            verify(db).execSQL(startsWith("CREATE TABLE " + EventRecord.TABLE_NAME));
        }
    }

}