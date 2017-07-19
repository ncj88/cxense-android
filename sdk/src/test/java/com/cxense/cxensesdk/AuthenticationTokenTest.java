package com.cxense.cxensesdk;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-14).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AuthenticationToken.class})
@PowerMockIgnore({"javax.crypto.*"})
public class AuthenticationTokenTest {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    private Date date;

    @Before
    public void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTimeInMillis(1);
        AuthenticationToken.DATE_FORMAT.setTimeZone(TIME_ZONE);
        date = calendar.getTime();
        whenNew(Date.class).withNoArguments().thenReturn(date);
    }

    @Test
    public void create() throws Exception {
        String username = "testUser", secret = "secret";
        String token = AuthenticationToken.create(username, secret);
        assertThat(token, allOf(
                containsString("username=" + username),
                containsString("date=" + AuthenticationToken.DATE_FORMAT.format(date)),
                containsString("hmac-sha256-hex=6AD01B3F631B2F4E7EDE5ACD1BA888E0D262F4F3976D651E3049F6287053BE20")
        ));
    }

}