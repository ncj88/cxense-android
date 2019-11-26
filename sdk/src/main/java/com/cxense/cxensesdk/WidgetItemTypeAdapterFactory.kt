package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.WidgetItem
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

class WidgetItemTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        @Suppress("UNCHECKED_CAST")
        return if (WidgetItem::class.java.isAssignableFrom(type.rawType))
            WidgetItemTypeAdapter(
                gson.getAdapter(JsonElement::class.java)
            ) as TypeAdapter<T>?
        else null
    }
}
