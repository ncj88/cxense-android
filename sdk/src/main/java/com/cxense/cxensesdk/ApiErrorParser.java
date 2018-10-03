package com.cxense.cxensesdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cxense.cxensesdk.exceptions.BadRequestException;
import com.cxense.cxensesdk.exceptions.CxenseException;
import com.cxense.cxensesdk.exceptions.ForbiddenException;
import com.cxense.cxensesdk.exceptions.NotAuthorizedException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-09-17).
 */
public class ApiErrorParser {
    private final Converter<ResponseBody, ApiError> converter;

    public ApiErrorParser(Converter<ResponseBody, ApiError> converter) {
        this.converter = converter;
    }

    @Nullable
    public CxenseException parseError(@NonNull Response<?> response) {
        if (response.isSuccessful())
            return null;
        try {
            ApiError apiError;
            try {
                apiError = converter.convert(response.errorBody());
            } catch (IOException ex) {
                apiError = new ApiError();
            }
            String message = apiError.error != null ? apiError.error : "";
            switch (response.code()) {
                case 400:
                    return new BadRequestException(message);
                case 401:
                    return new NotAuthorizedException(message);
                case 403:
                    return new ForbiddenException(message);
                default:
                    return new CxenseException(message);
            }
        } catch (Exception e) {
            return new CxenseException(e.getMessage());
        }
    }
}
