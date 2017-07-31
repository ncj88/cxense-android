package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class UserTest {
    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getProfiles() throws Exception {
        user.profiles = new ArrayList<>();
        user.getProfiles().add(new UserProfile());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getIdentities() throws Exception {
        user.identities = new ArrayList<>();
        user.getIdentities().add(new UserIdentity());
    }

}