package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

import com.cxense.Preconditions;

/**
 * User identity object.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public class UserIdentity extends BaseUserIdentity {
    protected UserIdentity() {
        super();
    }

    protected UserIdentity(String type) {
        super(type);
    }

    /**
     * Creates new instance with user identifier value and user identifier type.
     *
     * @param id   user identifier
     * @param type user identifier type
     */
    public UserIdentity(@NonNull String id, @NonNull String type) {
        super(id, type);
        Preconditions.checkStringForNullOrEmpty(id, "id");
    }

    /**
     * Creates new instance from another {@link UserIdentity}
     *
     * @param identity UserIdentity instance
     */
    public UserIdentity(@NonNull UserIdentity identity) {
        this(identity.id, identity.type);
    }
}
