package com.cxense.cxensesdk;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-07).
 */

public class NotifyingSendTask extends SendTask {
    private final Object syncObject;

    public NotifyingSendTask(Object syncObject, DependenciesProvider provider) {
        super(provider.getApi(), provider.getEventRepository(), provider.getCxenseConfiguration(),
                provider.getDeviceInfoProvider(), provider.getUserProvider(), provider.getMapper(),
                provider.getPerformanceEventConverter(), provider.getErrorParser(), provider.getEventsSendCallback());
        this.syncObject = syncObject;
    }

    @Override
    public void run() {
        super.run();
        synchronized (syncObject) {
            syncObject.notify();
        }
    }
}
