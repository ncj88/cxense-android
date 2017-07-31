package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-31).
 */
public class CxenseUserIdentityTest extends BaseTest {
    private CxenseUserIdentity userIdentity;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        userIdentity = new CxenseUserIdentity("type", "id");
    }

    @Test
    public void getCxenseId() throws Exception {
        String cxenseId = "cxenseId";
        Whitebox.setInternalState(userIdentity, "cxenseId", cxenseId);
        assertEquals(cxenseId, userIdentity.getCxenseId());
    }

    @Test
    public void setCxenseId() throws Exception {
        String cxenseId = "cxenseId";
        userIdentity.setCxenseId(cxenseId);
        assertEquals(cxenseId, Whitebox.getInternalState(userIdentity, "cxenseId"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCxenseIdNull() throws Exception {
        userIdentity.setCxenseId(null);
    }
}