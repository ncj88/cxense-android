package com.cxense.cxensesdk;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RawJsonAdapter extends TypeAdapter<List<String>> {
    @Override
    public void write(JsonWriter out, List<String> value) throws IOException {
        out.beginArray();
        for (String s : value) {
            out.jsonValue(s);
        }
        out.endArray();
    }

    @Override
    public List<String> read(JsonReader in) throws IOException {
        // not optimized version
        List<String> data = new ArrayList<>();
        in.beginArray();
        while (in.hasNext())
            data.add(in.nextString());
        in.endArray();
        return data;
    }
}
