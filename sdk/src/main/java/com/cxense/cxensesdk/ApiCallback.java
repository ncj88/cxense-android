package com.cxense.cxensesdk;

import com.cxense.cxensesdk.exceptions.CxenseException;

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
    private final CxenseSdk cxenseInstance;

    ApiCallback(LoadCallback<T> callback, CxenseSdk cxense) {
        this.callback = callback;
        cxenseInstance = cxense;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            if (callback != null)
                callback.onSuccess(response.body());
            return;
        }
        CxenseException exception = cxenseInstance.parseError(response);
        if (callback != null)
            callback.onError(exception);
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        if (callback != null)
            callback.onError(throwable);
    }
}
