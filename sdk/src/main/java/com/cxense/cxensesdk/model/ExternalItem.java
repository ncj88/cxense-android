package com.cxense.cxensesdk.model;

import android.support.annotation.NonNull;

import com.cxense.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

/**
 * Stored key-values for the user.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
final class ExternalItem {
    private static final int GROUP_NAME_MAX_LENGTH = 30;
    private static final int ITEM_NAME_MAX_LENGTH = 40;
    private static final String ERROR_MSG = "'%s' can contains only letters and digits, max length is %d";
    @JsonProperty("group")
    String group;
    @JsonProperty("item")
    String item;

    private ExternalItem() {
    }

    ExternalItem(@NonNull String group, @NonNull String item) {
        this();
        Preconditions.checkStringForRegex(group, "group", String.format(Locale.US, "^[\\w]{1,%d}$", GROUP_NAME_MAX_LENGTH), ERROR_MSG, "group", GROUP_NAME_MAX_LENGTH);
        Preconditions.checkStringForRegex(item, "item", String.format(Locale.US, "^[\\w]{1,%d}$", ITEM_NAME_MAX_LENGTH), ERROR_MSG, "item", ITEM_NAME_MAX_LENGTH);
        this.group = group;
        this.item = item;
    }

    /**
     * Gets the group name.
     *
     * @return group name
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets the item which is to be associated with the group name.
     *
     * @return item
     */
    public String getItem() {
        return item;
    }
}
