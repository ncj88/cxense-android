package com.cxense.cxensesdk

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri

open class CxSdkInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let { initCxense(it) }
        return false
    }

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        checkNotNull(info) {
            "CxInitProvider ProviderInfo cannot be null."
        }
        checkAttachInfo(info)
        super.attachInfo(context, info)
    }

    /**
     * Initialize Cxense SDK.
     *
     * @param context {@code Context} instance from {@code Activity}/{@code ContentProvider}/etc.
     */
    protected fun initCxense(context: Context) = DependenciesProvider.init(context)

    internal open fun checkAttachInfo(info: ProviderInfo) {
        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        check(BuildConfig.AUTHORITY != info.authority) {
            "Incorrect provider authority in manifest. Most likely due to a missing applicationId variable" +
                    " in application's build.gradle."
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int =
        0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}
