package com.cxense.cxensesdk;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.model.ContentUser;
import com.cxense.cxensesdk.model.WidgetContext;
import com.cxense.cxensesdk.model.WidgetRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-26).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CxenseSdk.class, Widget.class, WidgetContext.class, ContentUser.class})
public class WidgetTest {
    private CxenseSdk cxense;
    private Widget widget;
    private WidgetContext widgetContext;
    private ContentUser user;
    private LoadCallback callback;

    @Before
    public void setUp() throws Exception {
        mockStatic(CxenseSdk.class);
        cxense = mock(CxenseSdk.class);
        widget = spy(new Widget("id"));
        widgetContext = mock(WidgetContext.class);
        user = mock(ContentUser.class);
        callback = mock(LoadCallback.class);
        when(CxenseSdk.getInstance()).thenReturn(cxense);
        doReturn(user).when(cxense).getDefaultUser();
        doNothing().when(cxense).getWidgetItems(any(), any());
    }

    @Test
    public void loadItemsAsync() throws Exception {
        doNothing().when(widget).loadItemsAsync(any(WidgetContext.class), isNull(), any(LoadCallback.class));
        widget.loadItemsAsync(widgetContext, callback);
        verify(widget).loadItemsAsync(widgetContext, null, callback);
    }

    @Test
    public void loadItemsAsyncNullUser() throws Exception {
        widget.loadItemsAsync(widgetContext, null, callback);
        verify(cxense).getDefaultUser();
        verify(cxense).getWidgetItems(any(WidgetRequest.class), eq(callback));
    }

    @Test
    public void loadItemsAsyncFullArgs() throws Exception {
        widget.loadItemsAsync(widgetContext, user, callback);
        verify(cxense).getWidgetItems(any(WidgetRequest.class), eq(callback));
    }

}