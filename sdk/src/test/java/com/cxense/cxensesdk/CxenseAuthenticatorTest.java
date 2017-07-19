package com.cxense.cxensesdk;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-14).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationToken.class, Route.class, Response.class})
public class CxenseAuthenticatorTest {
    private static final String HEADER = "username=testUser date=1970-01-01T04:00:00.001+0400 hmac-sha256-hex=C266EF8D0CF01BE4ADA9DC6E4D4DBB9870DE3BD3A20BB3E060326D691597382A";
    private CxenseAuthenticator authenticator;

    @Before
    public void setUp() throws Exception {
        mockStatic(AuthenticationToken.class);
        when(AuthenticationToken.create(anyString(), anyString())).thenReturn(HEADER);
        authenticator = new CxenseAuthenticator();
    }

    @Test
    public void updateCredentials() throws Exception {
        String username = "user", apiKey = "key";
        authenticator.updateCredentials(username, apiKey);
        assertEquals(username, Whitebox.getInternalState(authenticator, "username"));
        assertEquals(apiKey, Whitebox.getInternalState(authenticator, "apiKey"));
    }

    @Test
    public void authenticate() throws Exception {
        Route route = mock(Route.class);
        Response response = mock(Response.class);
        Request request = new Request.Builder().url("http://example.com/").build();
        when(response.request()).thenReturn(request);
        Request result = authenticator.authenticate(route, response);
        assertEquals(HEADER, result.header(CxenseAuthenticator.AUTH_HEADER));
    }

}