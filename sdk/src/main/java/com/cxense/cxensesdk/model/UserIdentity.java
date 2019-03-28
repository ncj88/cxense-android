package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    protected UserIdentity(@Nullable String type) {
        super(type);
    }

    /**
     * Creates new instance with user identifier value and user identifier type.
     *
     * @param id   user identifier
     * @param type user identifier type
     */
    public UserIdentity(@Nullable String id, @Nullable String type) {
        super(id, type);
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
