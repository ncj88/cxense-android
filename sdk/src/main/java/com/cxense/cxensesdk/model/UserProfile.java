package com.cxense.cxensesdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * User generated content profile
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
public final class UserProfile {
    @JsonProperty("item")
    String item;
    @JsonProperty("groups")
    List<UserProfileGroup> groups;

    /**
     * Gets item extracted or generated form the page content. Usually a string or keyword extracted from the page.
     *
     * @return item value
     */
    public String getItem() {
        return item;
    }

    /**
     * Gets profile group objects associated with the item.
     *
     * @return list of {@link UserProfileGroup} objects
     */
    public List<UserProfileGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }
}
