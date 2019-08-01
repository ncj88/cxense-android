package com.cxense.cxensesdk.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


/**
 * Base identity object for group of users, that must contains only type of users
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public class BaseUserIdentity {
    public static final String ID = "id";
    public static final String TYPE = "type";
    @SerializedName(ID)
    String id;
    @SerializedName(TYPE)
    String type;

    protected BaseUserIdentity() {
    }

    /**
     * Creates new instance of object with user identifier value and user identifier type
     *
     * @param id   user identifier value (my be null for users group)
     * @param type user identifier type
     */
    public BaseUserIdentity(@Nullable String id, @Nullable String type) {
        this(type);
        this.id = id;
    }

    /**
     * Creates new instance of object with user identifier value and user identifier type
     *
     * @param type user identifier type
     */
    public BaseUserIdentity(@Nullable String type) {
        this();
        this.type = type;
    }

    /**
     * Gets user identifier value
     *
     * @return user identifier
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Gets user identifier type
     *
     * @return user identifier type
     */
    @Nullable
    public String getType() {
        return type;
    }
}
