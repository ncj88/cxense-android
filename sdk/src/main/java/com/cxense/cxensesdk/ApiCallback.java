package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Basic callback for API
 *
 * @param <T> success data type
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
class ApiCallback<T> implements Callback<T> {
    private final LoadCallback<T> callback;
    private final ApiErrorParser errorParser;

    ApiCallback(LoadCallback<T> callback, ApiErrorParser errorParser) {
        this.callback = callback;
        this.errorParser = errorParser;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (callback == null)
            return;

        if (response.isSuccessful()) {
            callback.onSuccess(response.body());
        } else callback.onError(errorParser.parseError(response));
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
        if (callback != null)
            callback.onError(throwable);
    }
}
