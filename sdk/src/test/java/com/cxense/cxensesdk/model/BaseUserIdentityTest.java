package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class BaseUserIdentityTest {
    private BaseUserIdentity userIdentity;

    @Before
    public void setUp() throws Exception {
        userIdentity = new BaseUserIdentity();
    }

    @Test
    public void getId() throws Exception {
        String id = "id";
        userIdentity.id = id;
        assertEquals(id, userIdentity.getId());
    }

    @Test
    public void getType() throws Exception {
        String type = "type";
        userIdentity.type = type;
        assertEquals(type, userIdentity.getType());
    }

}