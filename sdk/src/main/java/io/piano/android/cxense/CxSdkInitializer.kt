package io.piano.android.cxense

import android.content.Context
import androidx.startup.Initializer

@Suppress("unused")
class CxSdkInitializer : Initializer<CxenseSdk> {
    override fun create(context: Context): CxenseSdk = DependenciesProvider.init(context).cxenseSdk

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
