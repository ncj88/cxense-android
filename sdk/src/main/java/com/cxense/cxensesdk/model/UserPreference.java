package com.cxense.cxensesdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User preferences. Used for likes/dislikes.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

public final class UserPreference {
    /**
     * List of categories.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public List<String> categories;
    /**
     * A boost value between 0.0 and 100.0 indicating the level of boost to give for the list of categories.
     * Note that 0.0 (the default) has the special handling of being a filter instead of a boost.
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public double boost;

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public UserPreference() {
        categories = new ArrayList<>();
    }

    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public UserPreference(List<String> categories, double boost) {
        this();
        this.categories.addAll(categories);
        this.boost = boost;
    }
}
