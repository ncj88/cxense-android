package com.cxense.cxensesdk.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-26).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WidgetContext.class})
public class WidgetContextBuilderTest {
    private WidgetContext widgetContext;
    private WidgetContext.Builder builder;

    @Before
    public void setUp() throws Exception {
        widgetContext = mock(WidgetContext.class);
        builder = new WidgetContext.Builder("url");
        whenNew(WidgetContext.class).withAnyArguments().thenReturn(widgetContext);
    }

    @Test
    public void setUrl() throws Exception {
        String url = "new_url";
        assertThat(builder, is(builder.setUrl(url)));
        assertEquals(url, Whitebox.getInternalState(builder, "url"));
    }

    @Test
    public void setPageClass() throws Exception {
        String pageClass = "pageClass";
        assertThat(builder, is(builder.setPageClass(pageClass)));
        assertEquals(pageClass, Whitebox.getInternalState(builder, "pageClass"));
    }

    @Test
    public void setSentiment() throws Exception {
        String sentiment = "sentiment";
        assertThat(builder, is(builder.setSentiment(sentiment)));
        assertEquals(sentiment, Whitebox.getInternalState(builder, "sentiment"));
    }

    @Test
    public void setRecommending() throws Exception {
        assertThat(builder, is(builder.setRecommending(true)));
        assertTrue(Whitebox.getInternalState(builder, "recommending"));
    }

    @Test
    public void setCategories() throws Exception {
        Map<String, String> categories = new HashMap<>();
        assertThat(builder, is(builder.setCategories(categories)));
        Map<String, String> map = Whitebox.getInternalState(builder, "categories");
        assertEquals(categories.size(), map.size());
    }

    @Test
    public void setKeywords() throws Exception {
        List<String> keywords = new ArrayList<>();
        assertThat(builder, is(builder.setKeywords(keywords)));
        List<String> list = Whitebox.getInternalState(builder, "keywords");
        assertEquals(keywords.size(), list.size());
    }

    @Test
    public void setNeighbors() throws Exception {
        List<String> neighbors = new ArrayList<>();
        assertThat(builder, is(builder.setNeighbors(neighbors)));
        List<String> list = Whitebox.getInternalState(builder, "neighbors");
        assertEquals(neighbors.size(), list.size());
    }

    @Test
    public void setReferrer() throws Exception {
        String referrer = "referrer";
        assertThat(builder, is(builder.setReferrer(referrer)));
        assertEquals(referrer, Whitebox.getInternalState(builder, "referrer"));
    }

    @Test
    public void setParameters() throws Exception {
        List<ContextParameter> parameters = new ArrayList<>();
        parameters.add(new ContextParameter("key", "value"));
        assertThat(builder, is(builder.setParameters(parameters)));
        List<ContextParameter> list = Whitebox.getInternalState(builder, "parameters");
        assertEquals(parameters.size(), list.size());
    }

    @Test
    public void build() throws Exception {
        assertThat(widgetContext, is(builder.build()));
    }

}