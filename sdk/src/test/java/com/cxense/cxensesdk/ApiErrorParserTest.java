package com.cxense.cxensesdk;

import com.cxense.cxensesdk.exceptions.BadRequestException;
import com.cxense.cxensesdk.exceptions.CxenseException;
import com.cxense.cxensesdk.exceptions.ForbiddenException;
import com.cxense.cxensesdk.exceptions.NotAuthorizedException;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-18).
 */
public class ApiErrorParserTest {
    private Converter<ResponseBody, ApiError> errorConverter;
    private ApiErrorParser errorParser;

    @Before
    public void setUp() throws Exception {
        errorConverter = mock(Converter.class);
        errorParser = new ApiErrorParser(errorConverter);
    }

    @Test
    public void parseErrorException() throws Exception {
        Response response = Response.error(400, mock(ResponseBody.class));
        when(errorConverter.convert(any(ResponseBody.class))).thenThrow(new IOException());
        CxenseException result = errorParser.parseError(response);
        assertThat(result, both(isA(CxenseException.class)).and(notNullValue()));
    }

    @Test
    public void parseErrorApiErrorNull() {
        Response response = Response.error(400, mock(ResponseBody.class));

    }

    @Test
    public void parseErrorResponseSuccesfull() throws Exception {
        Response response = Response.success(mock(ResponseBody.class));
        assertNull(errorParser.parseError(response));
    }

    @Test
    public void onResponse400() throws Exception {
        checkException(400, BadRequestException.class);
    }

    @Test
    public void onResponse401() throws Exception {
        checkException(401, NotAuthorizedException.class);
    }

    @Test
    public void onResponse403() throws Exception {
        checkException(403, ForbiddenException.class);
    }

    @Test
    public void onResponse4XX() throws Exception {
        checkException(418, CxenseException.class);
    }

    private void checkException(int code, Class<? extends CxenseException> clazz) throws Exception {
        Response response = Response.error(code, mock(ResponseBody.class));
        when(errorConverter.convert(any(ResponseBody.class))).thenReturn(new ApiError());
        assertThat(errorParser.parseError(response), instanceOf(clazz));
    }

}