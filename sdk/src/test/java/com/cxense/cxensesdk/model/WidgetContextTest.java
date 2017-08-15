package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-26).
 */
public class WidgetContextTest {
    private WidgetContext widgetContext;

    @Before
    public void setUp() throws Exception {
        widgetContext = new WidgetContext(new WidgetContext.Builder("url"));
    }

    @Test
    public void getUrl() throws Exception {
        String url = "new_url";
        Whitebox.setInternalState(widgetContext, "url", url);
        assertEquals(url, widgetContext.getUrl());
    }

    @Test
    public void getPageClass() throws Exception {
        String pageClass = "pageClass";
        Whitebox.setInternalState(widgetContext, "pageClass", pageClass);
        assertEquals(pageClass, widgetContext.getPageClass());
    }

    @Test
    public void getSentiment() throws Exception {
        String sentiment = "sentiment";
        Whitebox.setInternalState(widgetContext, "sentiment", sentiment);
        assertEquals(sentiment, widgetContext.getSentiment());
    }

    @Test
    public void isRecommending() throws Exception {
        Whitebox.setInternalState(widgetContext, "recommending", true);
        assertTrue(widgetContext.isRecommending());
    }

    @Test
    public void getCategories() throws Exception {
        Map<String, String> categories = new HashMap<>();
        Whitebox.setInternalState(widgetContext, "categories", categories);
        assertThat(categories, is(widgetContext.getCategories()));
    }

    @Test
    public void getKeywords() throws Exception {
        List<String> keywords = new ArrayList<>();
        Whitebox.setInternalState(widgetContext, "keywords", keywords);
        assertThat(keywords, is(widgetContext.getKeywords()));
    }

    @Test
    public void getNeighbors() throws Exception {
        List<String> neighbors = new ArrayList<>();
        Whitebox.setInternalState(widgetContext, "neighbors", neighbors);
        assertThat(neighbors, is(widgetContext.getNeighbors()));
    }

    @Test
    public void getReferrer() throws Exception {
        String referrer = "referrer";
        Whitebox.setInternalState(widgetContext, "referrer", referrer);
        assertEquals(referrer, widgetContext.getReferrer());
    }

    @Test
    public void getParameters() throws Exception {
        List<ContextParameter> parameters = new ArrayList<>();
        Whitebox.setInternalState(widgetContext, "parameters", parameters);
        assertThat(parameters, is(widgetContext.getParameters()));
    }

}