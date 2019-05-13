package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.ConversionEvent;
import com.cxense.cxensesdk.model.UserIdentity;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;

public class ConversionEventTest extends BaseTest {
    private ConversionEvent event;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependenciesProvider.init(context);
        event = spy(new ConversionEvent.Builder(new ArrayList<>(), "siteID", "productId", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT).build());
    }

    @Test
    public void getType() {
        String type = "type";
        Whitebox.setInternalState(event, "type", type);
        assertEquals(type, event.getType());
    }

    @Test
    public void getIdentities() {
        List<UserIdentity> identities = new ArrayList<>();
        Whitebox.setInternalState(event, "identities", identities);
        assertThat(identities, is(event.getIdentities()));
    }

    @Test
    public void getSiteId() {
        String siteId = "siteId";
        Whitebox.setInternalState(event, "siteId", siteId);
        assertEquals(siteId, event.getSiteId());
    }

    @Test
    public void getProductId() {
        String productId = "productId";
        Whitebox.setInternalState(event, "productId", productId);
        assertEquals(productId, event.getProductId());
    }

    @Test
    public void getPrice() {
        Double price = 1.2;
        Whitebox.setInternalState(event, "price", price);
        assertEquals(price, event.getPrice());
    }

    @Test
    public void getRenewalFrequency() {
        String renewalFrequency = "renewalFrequency";
        Whitebox.setInternalState(event, "renewalFrequency", renewalFrequency);
        assertEquals(renewalFrequency, event.getRenewalFrequency());
    }

    @Test
    public void getFunnelStep() {
        String funnelStep = "funnelStep";
        Whitebox.setInternalState(event, "funnelStep", funnelStep);
        assertEquals(funnelStep, event.getFunnelStep());
    }
}