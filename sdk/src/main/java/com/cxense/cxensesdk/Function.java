package com.cxense.cxensesdk;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

/**
 * Represents a apply function that accepts one argument and produces a result.
 * This is interface backport from Java 8
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@FunctionalInterface
public interface Function<T, R> {
    @Nullable
    R apply(@Nullable T t);
}
