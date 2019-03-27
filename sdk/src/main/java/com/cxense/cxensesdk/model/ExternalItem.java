package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.Preconditions;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

/**
 * Stored key-values for the user.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class ExternalItem {
    private static final String GROUP_REGEXP = "^[a-zA-Z\\d-]{1,%d}$";
    private static final int GROUP_NAME_MAX_LENGTH = 26;
    private static final int ITEM_NAME_MAX_LENGTH = 100;
    private static final String ERROR_MSG = "'%s' can contains only letters, digits or dash, max length is %d";
    @JsonProperty("group")
    String group;
    @JsonProperty("item")
    String item;

    private ExternalItem() {
    }

    ExternalItem(@NonNull String group, @NonNull String item) {
        this();
        Preconditions.checkStringForRegex(group, "group", String.format(Locale.US, GROUP_REGEXP,
                GROUP_NAME_MAX_LENGTH), ERROR_MSG, "group", GROUP_NAME_MAX_LENGTH);
        Preconditions.checkStringMaxLength(item, "item", ITEM_NAME_MAX_LENGTH);
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
