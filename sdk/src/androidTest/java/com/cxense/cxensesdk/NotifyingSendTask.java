package com.cxense.cxensesdk;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-07).
 */

public class NotifyingSendTask extends CxenseSdk.SendTask {
    private final Object syncObject;

    public NotifyingSendTask(Object syncObject) {
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
