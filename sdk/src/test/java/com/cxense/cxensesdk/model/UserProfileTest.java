package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class UserProfileTest {
    private UserProfile userProfile;

    @Before
    public void setUp() throws Exception {
        userProfile = new UserProfile();
    }

    @Test
    public void getItem() throws Exception {
        String item = "item";
        userProfile.item = item;
        assertEquals(item, userProfile.getItem());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getGroups() throws Exception {
        userProfile.groups = new ArrayList<>();
        userProfile.getGroups().add(new UserProfileGroup());
    }
}