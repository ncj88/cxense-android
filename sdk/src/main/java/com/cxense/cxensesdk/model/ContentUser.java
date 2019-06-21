package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class describes user for server.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-08-14).
 */

public class ContentUser {
    /**
     * Map from ID-types to IDs (String to String). ID-types are defined as a customer-prefix.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @NonNull
    public final Map<String, String> ids;
    /**
     * User's likes
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public UserPreference likes;
    /**
     * User's dislikes
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    @Nullable
    public UserPreference dislikes;

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public ContentUser() {
        ids = new HashMap<>();
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public ContentUser(@Nullable String userId) {
        this();
        ids.put("usi", userId);
    }
}
