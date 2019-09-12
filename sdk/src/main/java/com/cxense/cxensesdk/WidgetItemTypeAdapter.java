package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.WidgetItem;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetItemTypeAdapter extends TypeAdapter<WidgetItem> {

    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String CLICK_URL = "click_url";

    private final TypeAdapter<JsonElement> jsonElementTypeAdapter;

    public WidgetItemTypeAdapter(TypeAdapter<JsonElement> jsonElementTypeAdapter) {
        this.jsonElementTypeAdapter = jsonElementTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, WidgetItem value) throws IOException {
        out.beginObject()
                .name(TITLE)
                .value(value.title)
                .name(URL)
                .value(value.url)
                .name(CLICK_URL)
                .value(value.clickUrl);
        for (Map.Entry<String, Object> entry : value.properties.entrySet()) {
            out.name(entry.getKey()).value(entry.getValue().toString());
        }
        out.endObject();
    }

    @Override
    public WidgetItem read(JsonReader in) throws IOException {
        String title = null, url = null, clickUrl = "";
        Map<String, Object> properties = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case TITLE:
                    title = in.nextString();
                    break;
                case URL:
                    url = in.nextString();
                    break;
                case CLICK_URL:
                    clickUrl = in.nextString();
                    break;
                default:
                    switch (in.peek()) {
                        case BEGIN_ARRAY:
                            in.beginArray();
                            List<String> value = new ArrayList<>();
                            while (in.peek() != JsonToken.END_ARRAY)
                                value.add(in.nextString());
                            in.endArray();
                            properties.put(name, value);
                            break;
                        case BEGIN_OBJECT:
                            JsonElement obj = jsonElementTypeAdapter.read(in);
                            properties.put(name, obj);
                            break;
                        default:
                            properties.put(name, in.nextString());
                            break;
                    }
            }
        }
        in.endObject();
        return new WidgetItem(title, url, clickUrl, properties);
    }
}
