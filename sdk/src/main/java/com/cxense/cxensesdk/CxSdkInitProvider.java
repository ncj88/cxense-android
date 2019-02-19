package com.cxense.cxensesdk;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * ContentProvider for init magic. It doesn't provide any content, only auto init.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public class CxSdkInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        Context context = getContext();
        initCxense(context);
        return false;
    }

    void checkAttachInfo(ProviderInfo info) {
        if (info == null) {
            throw new NullPointerException("CxInitProvider ProviderInfo cannot be null.");
        }
        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        // TODO: document this for new SDKs
        if ((getAuthority()).equals(info.authority)) {
            throw new IllegalStateException("Incorrect provider authority in manifest. Most likely due to a "
                    + "missing applicationId variable in application\'s build.gradle.");
        }
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        checkAttachInfo(info);
        super.attachInfo(context, info);
    }

    /**
     * Initialize Cxense SDK.
     *
     * @param context {@code Context} instance from {@code Activity}/{@code ContentProvider}/etc.
     */
    protected void initCxense(Context context) {
        DependenciesProvider.init(context);
    }

    /**
     * Gets provider authority.
     *
     * @return package name
     */
    @NonNull
    protected String getAuthority() {
        return BuildConfig.AUTHORITY;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
