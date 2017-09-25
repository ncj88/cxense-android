package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-28).
 */
@PrepareForTest({UserExternalData.class})
public class UserExternalDataBuilderTest extends BaseTest {
    private UserExternalData externalData;
    private UserExternalData.Builder builder;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        externalData = mock(UserExternalData.class);
        builder = new UserExternalData.Builder(new UserIdentity("id", "type"));
        whenNew(UserExternalData.class).withAnyArguments().thenReturn(externalData);
    }

    @Test
    public void setIdentity() throws Exception {
        UserIdentity identity = new UserIdentity("id", "type");
        assertThat(builder, is(builder.setIdentity(identity)));
        assertThat(identity, is((UserIdentity) Whitebox.getInternalState(builder, "identity")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIdentityNull() throws Exception {
        builder.setIdentity(null);
    }

    @Test
    public void addExternalItem() throws Exception {
        List<ExternalItem> items = Whitebox.getInternalState(builder, "items");
        int size = items.size();
        assertThat(builder, is(builder.addExternalItem("group", "item")));
        assertEquals(size + 1, items.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExternalItemMany() throws Exception {
        for (int i = 0; i < UserExternalData.MAX_PROFILE_ITEMS + 1; i++)
            builder.addExternalItem("group", "item");
    }

    @Test
    public void build() throws Exception {
        assertThat(externalData, is(builder.build()));
    }

}