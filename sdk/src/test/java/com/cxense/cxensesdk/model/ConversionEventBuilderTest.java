package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;

public class ConversionEventBuilderTest extends BaseTest {
    private ConversionEvent.Builder builder;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        builder = spy(new ConversionEvent.Builder(new ArrayList<>(), "siteId", "productId", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT));
    }

    @Test
    public void addIdentity() {
        List<UserIdentity> identities = Whitebox.getInternalState(builder, "identities");
        int size = identities.size();
        assertThat(builder, is(builder.addIdentity(new UserIdentity("id", "type"))));
        assertEquals(size + 1, identities.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIdentityNull() throws Exception {
        builder.addIdentity(null);
    }

    @Test
    public void addIdentities() throws Exception {
        List<UserIdentity> identities = Whitebox.getInternalState(builder, "identities");
        List<UserIdentity> newIdentities = Arrays.asList(new UserIdentity("id", "type"),
                new UserIdentity("id", "type"));
        int size = identities.size();
        assertThat(builder, is(builder.addIdentities(newIdentities)));
        assertEquals(size + newIdentities.size(), identities.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addIdentitiesNull() throws Exception {
        builder.addIdentities(null);
    }

    @Test
    public void setSiteId() {
        String siteId = "siteId";
        assertThat(builder, is(builder.setSiteId(siteId)));
        assertEquals(siteId, Whitebox.getInternalState(builder, "siteId"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void setSiteIdNull() throws Exception {
        builder.setSiteId(null);
    }

    @Test
    public void setProductId() {
        String productId = "productId";
        assertThat(builder, is(builder.setProductId(productId)));
        assertEquals(productId, Whitebox.getInternalState(builder, "productId"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setProductIdNull() throws Exception {
        builder.setProductId(null);
    }

    @Test
    public void setPrice() {
        Double price = 1.2;
        assertThat(builder, is(builder.setPrice(price)));
        assertEquals(price, Whitebox.getInternalState(builder, "price"));
    }

    @Test
    public void setRenewalFrequency() {
        String renewalFrequency = "1wC";
        assertThat(builder, is(builder.setRenewalFrequency(renewalFrequency)));
        assertEquals(renewalFrequency, Whitebox.getInternalState(builder, "renewalFrequency"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRenewalFrequencyInvalid() {
        builder.setRenewalFrequency("qwerty");
    }

    @Test
    public void setFunnelStep() {
        String funnelStep = "funnelStep";
        assertThat(builder, is(builder.setFunnelStep(funnelStep)));
        assertEquals(funnelStep, Whitebox.getInternalState(builder, "funnelStep"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFunnelStepNull() throws Exception {
        builder.setFunnelStep(null);
    }
}