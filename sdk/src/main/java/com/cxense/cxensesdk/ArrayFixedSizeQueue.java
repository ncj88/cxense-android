package com.cxense.cxensesdk;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A bounded queue with fixed size backed by an * array.
 * This queue orders elements FIFO (first-in-first-out).  The
 * <em>head</em> of the queue is that element that has been on the
 * queue the longest time.  The <em>tail</em> of the queue is that
 * element that has been on the queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue. Attempts to
 * {@code put} an element into a full queue will result removing head element;
 * attempts to {@code take} an * element from an empty queue will similarly block.
 *
 * @param <E> the type of elements held in this queue
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class ArrayFixedSizeQueue<E> extends ArrayBlockingQueue<E> {
    private final int maxSize;

    public ArrayFixedSizeQueue(int capacity) {
        super(capacity);
        maxSize = capacity;
    }

    public ArrayFixedSizeQueue(int capacity, boolean fair) {
        super(capacity, fair);
        maxSize = capacity;
    }

    public ArrayFixedSizeQueue(int capacity, boolean fair, Collection<? extends E> c) {
        super(capacity, fair, c);
        maxSize = capacity;
    }

    /**
     * Inserts the specified element at the tail of this queue.
     * If queue is full, then head element will be removed before adding new element to queue.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean add(E e) {
        if (size() == maxSize) {
            // Queue is full
            remove();
        }
        return super.add(e);
    }
}
