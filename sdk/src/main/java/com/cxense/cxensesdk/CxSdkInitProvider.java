package com.cxense.cxensesdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cxense.CxInitProvider;

/**
 * ContentProvider for init magic. It doesn't provide any content, only auto init.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public class CxSdkInitProvider extends CxInitProvider {
    @Override
    protected void initCxense(Context context) {
        CxenseSdk.init(context);
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return BuildConfig.AUTHORITY;
    }
}
