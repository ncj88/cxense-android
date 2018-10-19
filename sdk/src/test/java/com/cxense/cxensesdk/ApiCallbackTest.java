package com.cxense.cxensesdk;

import com.cxense.cxensesdk.exceptions.CxenseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-13).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Response.class})
public class ApiCallbackTest {
    private LoadCallback<Object> loadCallback;
    private Call<Object> call;
    private Response<Object> response;
    private ApiCallback<Object> callback;

    @Before
    public void setUp() throws Exception {
        ApiErrorParser errorParser = mock(ApiErrorParser.class);
        loadCallback = mock(LoadCallback.class);
        call = mock(Call.class);
        response = mock(Response.class);
        callback = new ApiCallback<>(loadCallback, errorParser);
        ResponseBody errorBody = mock(ResponseBody.class);
        when(response.errorBody()).thenReturn(errorBody);
        CxenseException exception = new CxenseException();
        when(errorParser.parseError(any(Response.class))).thenReturn(exception);
    }

    @Test
    public void onResponseCallbackNull() {
        callback = new ApiCallback<>(null, null);
        callback.onResponse(call, response);
        verify(response, never()).isSuccessful();
    }

    @Test
    public void onResponseSuccessful() throws Exception {
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(new Object());
        callback.onResponse(call, response);
        verify(loadCallback).onSuccess(any());
    }

    @Test
    public void onResponseError() throws Exception {
        when(response.errorBody()).thenThrow(new IllegalStateException());
        when(response.isSuccessful()).thenReturn(false);
        checkException(200, CxenseException.class);
    }

    @Test
    public void onFailure() throws Exception {
        CxenseException exception = new CxenseException();
        callback.onFailure(call, exception);
        verify(loadCallback).onError(exception);
    }

    private void checkException(int code, Class<? extends CxenseException> clazz) {
        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(code);
        callback.onResponse(call, response);
        verify(loadCallback).onError(any(clazz));
    }

}