package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.WidgetItem;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class WidgetItemTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!WidgetItem.class.isAssignableFrom(type.getRawType()))
            return null;
        return (TypeAdapter<T>) new WidgetItemTypeAdapter(gson.getAdapter(JsonElement.class));
    }
}
