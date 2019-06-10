package com.cxense.cxensesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.cxense.cxensesdk.model.ContentUser;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2018-10-02).
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class UserProvider {
    @NonNull
    private final AdvertisingIdProvider advertisingIdProvider;
    @Nullable
    private String userId;
    @Nullable
    private ContentUser defaultUser;

    UserProvider(@NonNull AdvertisingIdProvider advertisingIdProvider) {
        this.advertisingIdProvider = advertisingIdProvider;
    }

    @Nullable
    public String getUserId() {
        if (userId == null)
            userId = advertisingIdProvider.getDefaultUserId();
        return userId;
    }

    void setUserId(@NonNull String id) {
        Preconditions.checkStringForRegex(id, "id", "^[\\w-+.]{16,}$",
                "The user id must be at least 16 characters long. Allowed characters are: " +
                        "A-Z, a-z, 0-9, \"_\", \"-\", \"+\" and \".\".");
        userId = id;
        defaultUser = null;
    }

    @NonNull
    ContentUser getContentUser() {
        if (defaultUser == null) {
            defaultUser = new ContentUser(getUserId());
        }
        return defaultUser;
    }
}
