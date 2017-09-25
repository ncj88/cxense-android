package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

import com.cxense.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Data associated with the user(s).
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess", "SameParameterValue", "UnusedReturnValue"})
// Public API.
public class UserExternalData extends UserIdentity {
    /**
     * Max external items for user.
     */
    public static final int MAX_PROFILE_ITEMS = 40;
    @JsonProperty("profile")
    List<ExternalItem> items;

    private UserExternalData() {
        super();
    }

    UserExternalData(Builder builder) {
        super(builder.identity);
        List<ExternalItem> itemList = new ArrayList<>(builder.items);
        for (ExternalItem item : itemList) {
            item.group = String.format(Locale.US, "%s-%s", type, item.group);
        }
        items = Collections.unmodifiableList(itemList);
    }

    /**
     * Gets stored key-values for the user.
     *
     * @return list of {@link ExternalItem} objects
     */
    public List<ExternalItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Helper class for building {@link UserExternalData}
     */
    public static class Builder {
        UserIdentity identity;
        List<ExternalItem> items;

        /**
         * Initialize Builder
         *
         * @param identity user identifier with type and id
         */
        public Builder(@NonNull UserIdentity identity) {
            items = new ArrayList<>();
            setIdentity(identity);
        }

        /**
         * Sets user identity data
         *
         * @param identity user identifier with type and id
         * @return builder instance
         */
        public Builder setIdentity(@NonNull UserIdentity identity) {
            Preconditions.checkForNull(identity, "identity");
            this.identity = identity;
            return this;
        }

        /**
         * Adds external item for user
         *
         * @param group The group name WITHOUT prefix.
         * @param item  The item which is to be associated with the group name.
         * @return builder instance
         * @throws IllegalArgumentException if you try to add more than {@link #MAX_PROFILE_ITEMS} items for user.
         */
        public Builder addExternalItem(@NonNull String group, @NonNull String item) {
            if (items.size() >= MAX_PROFILE_ITEMS)
                throw new IllegalArgumentException("Too many profile items");
            items.add(new ExternalItem(group, item));
            return this;
        }

        /**
         * Build {@link UserExternalData} instance.
         *
         * @return UserExternalData instance
         */
        public UserExternalData build() {
            return new UserExternalData(this);
        }
    }
}
