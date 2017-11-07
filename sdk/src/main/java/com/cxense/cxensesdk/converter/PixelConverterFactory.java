package com.cxense.cxensesdk.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-07).
 */

public class PixelConverterFactory extends Converter.Factory {
    public static PixelConverterFactory create() {
        return new PixelConverterFactory();
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        // TODO: write converter for pixel api
        return super.stringConverter(type, annotations, retrofit);
    }
}
