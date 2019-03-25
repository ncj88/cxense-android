package com.cxense.cxensesdk;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.model.ContentUser;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-02).
 */
public class UserProvider {
    private final AdvertisingIdProvider advertisingIdProvider;
    private String userId;
    private ContentUser defaultUser;

    public UserProvider(AdvertisingIdProvider advertisingIdProvider) {
        this.advertisingIdProvider = advertisingIdProvider;
    }

    public String getUserId() {
        if (userId == null)
            userId = advertisingIdProvider.getDefaultUserId();
        return userId;
    }

    public void setUserId(@NonNull String id) {
        Preconditions.checkStringForRegex(id, "id", "^[\\w-+.]{16,}$",
                "The user id must be at least 16 characters long. Allowed characters are: " +
                        "A-Z, a-z, 0-9, \"_\", \"-\", \"+\" and \".\".");
        userId = id;
        defaultUser = null;
    }

    public ContentUser getContentUser() {
        if (defaultUser == null) {
            defaultUser = new ContentUser(getUserId());
        }
        return defaultUser;
    }
}
