package com.cxense.cxensesdk.model;


import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-28).
 */
public class UserExternalDataTest extends BaseTest {
    private UserExternalData externalData;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        externalData = new UserExternalData(
                new UserExternalData.Builder(new UserIdentity("id", "type"))
                        .addExternalItem("group", "item"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getItems() throws Exception {
        externalData.getItems().add(new ExternalItem("group", "item"));
    }

}