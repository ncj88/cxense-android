package com.cxense.cxensesdk.model;

/**
 * Simple key-value parameter object
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class KeyValueParameter<K, V> {
    /**
     * Parameter key
     */
    public K key;
    /**
     * Parameter value
     */
    public V value;

    public KeyValueParameter() {
    }

    public KeyValueParameter(K key, V value) {
        this();
        this.key = key;
        this.value = value;
    }
}
