package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Test;

public class ExternalUserIdTest extends BaseTest {

    @Test
    public void createWithInternalId() {
        new ExternalUserId("cx", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithInternalIdError() {
        new ExternalUserId("cx", "test/");
    }
    @Test
    public void createWithExternalId() {
        new ExternalUserId("cxd", "test/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithExternalIdError() {
        new ExternalUserId("cxd", "test'");
    }
}