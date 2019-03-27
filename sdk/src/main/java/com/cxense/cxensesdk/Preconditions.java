package com.cxense.cxensesdk;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Helper class for checking parameters.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Check argument with function
     *
     * @param function    boolean function, that checks argument
     * @param arg         argument for checking
     * @param message     exception message
     * @param messageArgs args for formatting exception message
     * @param <T>         argument type
     * @throws IllegalArgumentException if function returns true
     */
    public static <T> void check(Function<T, Boolean> function, @Nullable T arg,
                                 @NonNull String message, Object... messageArgs) {
        if (function.apply(arg))
            throw new IllegalArgumentException(String.format(Locale.US, message, messageArgs));
    }

    /**
     * Checks argument for null
     *
     * @param arg  argument for checking
     * @param name argument name for exception message
     * @param <T>  argument type
     * @throws IllegalArgumentException if argument is null
     */
    public static <T> void checkForNull(@Nullable T arg, @NonNull String name) {
        check(v -> v == null, arg, "'%s' can't be null or empty", name);
    }

    /**
     * Checks string argument for null or empty
     *
     * @param arg  string argument for checking
     * @param name argument name for exception message
     * @throws IllegalArgumentException if string argument is null or empty
     */
    public static void checkStringForNullOrEmpty(@Nullable String arg, @NonNull String name) {
        check(TextUtils::isEmpty, arg, "'%s' can't be null or empty", name);
    }

    /**
     * Checks string argument for max length
     *
     * @param arg       string argument for checking
     * @param name      argument name for exception message
     * @param maxLength max length of argument
     * @throws IllegalArgumentException if string argument is null or longer than maxLength
     */
    public static void checkStringMaxLength(@Nullable String arg, @NonNull String name, int maxLength) {
        check(v -> v < 0, maxLength, "'maxLength' can't be less than 0");
        check(v -> v != null && v.length() > maxLength, arg, "'%s' can't be longer than %d symbols", name, maxLength);
    }

    /**
     * Checks string argument for min length
     *
     * @param arg       string argument for checking
     * @param name      argument name for exception message
     * @param minLength max length of argument
     * @throws IllegalArgumentException if string argument is null or shorter than minLength
     */
    public static void checkStringMinLength(@Nullable String arg, @NonNull String name, int minLength) {
        check(v -> v < 0, minLength, "'minLength' can't be less than 0");
        check(v -> v != null && v.length() < minLength, arg, "'%s' can't be shorter than %d symbols", name, minLength);
    }

    /**
     * Checks string argument for null or empty and max length
     *
     * @param arg       string argument for checking
     * @param name      argument name for exception message
     * @param maxLength max length of argument
     * @throws IllegalArgumentException if string argument is null or longer than maxLength
     */
    public static void checkStringNotNullMaxLength(@Nullable String arg, @NonNull String name, int maxLength) {
        checkStringForNullOrEmpty(arg, name);
        checkStringMaxLength(arg, name, maxLength);
    }

    /**
     * Checks string argument for null or empty and min length
     *
     * @param arg       string argument for checking
     * @param name      argument name for exception message
     * @param minLength max length of argument
     * @throws IllegalArgumentException if string argument is null or shorter than minLength
     */
    public static void checkStringNotNullMinLength(@Nullable String arg, @NonNull String name, int minLength) {
        checkStringForNullOrEmpty(arg, name);
        checkStringMinLength(arg, name, minLength);
    }

    /**
     * Checks string argument for matching regex
     *
     * @param arg         string argument for checking
     * @param name        argument name for exception message
     * @param regex       regex string
     * @param message     exception message
     * @param messageArgs args for formatting exception message
     * @throws IllegalArgumentException if string argument does not match regex
     */
    public static void checkStringForRegex(@Nullable String arg, @NonNull String name,
                                           @NonNull String regex, @NonNull String message,
                                           Object... messageArgs) {
        checkStringForNullOrEmpty(arg, name);
        check(v -> !v.matches(regex), arg, message, messageArgs);
    }

    /**
     * Checks string argument is URL
     *
     * @param arg         string argument for checking
     * @param name        argument name for exception message
     * @param message     exception message
     * @param messageArgs args for formatting exception message
     */
    public static void checkStringIsUrl(@Nullable String arg, @NonNull String name,
                                        @NonNull String message, Object... messageArgs) {
        checkStringForNullOrEmpty(arg, name);
        check(v -> !URLUtil.isNetworkUrl(v), arg, message, messageArgs);
    }
}
