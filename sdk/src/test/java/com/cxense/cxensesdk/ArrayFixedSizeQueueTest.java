package com.cxense.cxensesdk;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public class ArrayFixedSizeQueueTest {
    private static final int MAX_LENGTH = 3;

    @Test
    public void addSimple() throws Exception {
        tryAddddManyItems(new ArrayFixedSizeQueue<>(MAX_LENGTH));
    }

    @Test
    public void addWithAccessPolicy() throws Exception {
        tryAddddManyItems(new ArrayFixedSizeQueue<>(MAX_LENGTH, true));
    }

    @Test
    public void addIntoNotEmptyQueue() throws Exception {
        tryAddddManyItems(new ArrayFixedSizeQueue<>(MAX_LENGTH, true, Arrays.asList("0", "0")));
    }

    private void tryAddddManyItems(ArrayFixedSizeQueue<String> queue) {
        for (int i = 0; i < 5; i++) {
            queue.add(Integer.toString(i));
        }
        assertEquals(MAX_LENGTH, queue.size());
    }

}