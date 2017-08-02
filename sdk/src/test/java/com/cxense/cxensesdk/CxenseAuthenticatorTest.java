package com.cxense.cxensesdk;

import com.cxense.exceptions.CxenseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-14).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseAuthenticator.class, Route.class, Response.class})
@PowerMockIgnore({"javax.crypto.*"})
public class CxenseAuthenticatorTest {
    private static final String HEADER = "username=testUser date=1970-01-01T04:00:00.001+0400 hmac-sha256-hex=C266EF8D0CF01BE4ADA9DC6E4D4DBB9870DE3BD3A20BB3E060326D691597382A";
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    private Date date;
    private CxenseAuthenticator authenticator;
    private Route route;
    private Response response;
    private Request request;
    private int count = 0;

    @Before
    public void setUp() throws Exception {
        route = mock(Route.class);
        response = mock(Response.class);
        request = new Request.Builder().url("http://example.com/").build();
        when(response.request()).thenReturn(request);
        authenticator = spy(new CxenseAuthenticator());
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTimeInMillis(1);
        CxenseAuthenticator.DATE_FORMAT.setTimeZone(TIME_ZONE);
        date = calendar.getTime();
        whenNew(Date.class).withNoArguments().thenReturn(date);
    }

    @Test
    public void updateCredentials() throws Exception {
        String username = "user", apiKey = "key";
        authenticator.updateCredentials(username, apiKey);
        assertEquals(username, Whitebox.getInternalState(authenticator, "username"));
        assertEquals(apiKey, Whitebox.getInternalState(authenticator, "apiKey"));
    }

    @Test
    public void createToken() throws Exception {
        String username = "testUser", secret = "secret";
        String token = authenticator.createToken(username, secret);
        assertThat(token, allOf(
                containsString("username=" + username),
                containsString("date=" + CxenseAuthenticator.DATE_FORMAT.format(date)),
                containsString("hmac-sha256-hex=6AD01B3F631B2F4E7EDE5ACD1BA888E0D262F4F3976D651E3049F6287053BE20")
        ));
    }

    @Test
    public void authenticate() throws Exception {
        doReturn(HEADER).when(authenticator).createToken(anyString(), anyString());
        Request result = authenticator.authenticate(route, response);
        assertEquals(HEADER, result.header(CxenseAuthenticator.AUTH_HEADER));
    }

    @Test
    public void authenticateMaxRequests() throws Exception {
        doReturn(CxenseAuthenticator.DEFAULT_MAX_ATTEMPTS).when(authenticator).responseCount(response);
        assertNull(authenticator.authenticate(route, response));
    }

    @Test(expected = CxenseException.class)
    public void authenticateSomeException() throws Exception {
        doThrow(new InvalidKeyException()).when(authenticator).createToken(anyString(), anyString());
        authenticator.authenticate(route, response);
    }

    @Test
    public void responseCount() throws Exception {
        when(response.priorResponse()).thenAnswer(invocation -> {
            count++;
            return count < 2 ? response : null;
        });
        authenticator.responseCount(response);
    }

}