package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.ContentUser;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-15).
 */
public class UserProviderTest extends BaseTest {
    private AdvertisingIdProvider advertisingIdProvider;
    private UserProvider userProvider;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        advertisingIdProvider = mock(AdvertisingIdProvider.class);
        userProvider = spy(new UserProvider(advertisingIdProvider));
    }

    @Test
    public void getUserId() {
        userProvider.getUserId();
        verify(advertisingIdProvider).getDefaultUserId();
    }

    @Test
    public void getUserIdFilledBefore() {
        String id = "VeryVeryVeryGoodId";
        Whitebox.setInternalState(userProvider, "userId", id);
        assertEquals(id, userProvider.getUserId());
    }

    @Test
    public void setUserId() {
        String id = "VeryVeryVeryGoodId";
        userProvider.setUserId(id);
        assertEquals(id, Whitebox.getInternalState(userProvider, "userId"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserIdNull() throws Exception {
        userProvider.setUserId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserIdBad() throws Exception {
        userProvider.setUserId("BadId");
    }

    @Test
    public void getContentUser() {
        String id = "VeryVeryVeryGoodId";
        doReturn(id).when(userProvider).getUserId();
        assertNotNull(userProvider.getContentUser());
        verify(userProvider).getUserId();
    }

    @Test
    public void getContentUserFilledBefore() {
        ContentUser user = mock(ContentUser.class);
        Whitebox.setInternalState(userProvider, "defaultUser", user);
        assertEquals(user, userProvider.getContentUser());
    }
}