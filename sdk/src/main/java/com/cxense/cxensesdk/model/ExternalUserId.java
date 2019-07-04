package com.cxense.cxensesdk.model;

import androidx.annotation.NonNull;

import com.cxense.cxensesdk.Preconditions;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class ExternalUserId {
    @NonNull
    public String userType;
    @NonNull
    public String userId;

    /**
     * Initializes External User Id description.
     *
     * @param userType external user type
     * @param userId   external user id
     * @throws IllegalArgumentException if external user type has a length less than 1 or greater than 10 characters,
     *                                  if external user id has a length less than 1 or greater than 64 characters.
     */
    public ExternalUserId(@NonNull String userType, @NonNull String userId) {
        Preconditions.checkStringNotNullMaxLength(userType, "userType", 10);
        if (userType.equals("cx"))
        Preconditions.checkStringForRegex(userId, "userId", "^[\\w =@+\\-\\.]{1,64}$", "The valid characters for internal ids are digits, letters, space and the special characters \"=\", \"@\", \"+\", \"-\", \"_\" and \".\". Max length is 64 symbols");
        else
            Preconditions.checkStringForRegex(userId, "userId", "^[\\w !#$%&()*+,\\-\\.\\/;<=>?@\\[\\]^{}~|]{1,100}$", "The valid characters for external ids are digits, letters, space and the special characters !#$%%&()*+,-./;<=>?@[]^_{}~|. Max length is 100 symbols");
        this.userType = userType;
        this.userId = userId;
    }
}
