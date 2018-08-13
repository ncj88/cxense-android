package com.cxense.cxensesdk;

/**
 * Represents a apply function that accepts one argument and produces a result.
 * This is interface backport from Java 8
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
