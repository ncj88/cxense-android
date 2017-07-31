package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cxense.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base identity object for group of users, that must contains only type of users
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public class BaseUserIdentity {
    @JsonProperty("id")
    String id;
    @JsonProperty("type")
    String type;

    protected BaseUserIdentity() {
    }

    /**
     * Creates new instance of object with user identifier value and user identifier type
     *
     * @param id   user identifier value (my be null for users group)
     * @param type user identifier type
     */
    public BaseUserIdentity(@Nullable String id, @NonNull String type) {
        this(type);
        this.id = id;
    }

    /**
     * Creates new instance of object with user identifier value and user identifier type
     *
     * @param type user identifier type
     */
    public BaseUserIdentity(@NonNull String type) {
        this();
        Preconditions.checkStringForNullOrEmpty(type, "type");
        this.type = type;
    }

    /**
     * Gets user identifier value
     *
     * @return user identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Gets user identifier type
     *
     * @return user identifier type
     */
    public String getType() {
        return type;
    }
}
