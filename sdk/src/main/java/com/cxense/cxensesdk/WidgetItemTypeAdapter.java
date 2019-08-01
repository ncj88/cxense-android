package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.WidgetItem;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WidgetItemTypeAdapter extends TypeAdapter<WidgetItem> {

    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String CLICK_URL = "click_url";

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
            String value = in.nextString();
            switch (name) {
                case TITLE:
                    title = value;
                    break;
                case URL:
                    url = value;
                    break;
                case CLICK_URL:
                    clickUrl = value;
                    break;
                default:
                    properties.put(name, value);
            }
        }
        in.endObject();
        return new WidgetItem(title, url, clickUrl, properties);
    }
}
