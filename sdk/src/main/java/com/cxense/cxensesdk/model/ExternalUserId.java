package com.cxense.cxensesdk.model;

import com.cxense.cxensesdk.Preconditions;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
public final class ExternalUserId extends KeyValueParameter<String, String> {
    /**
     * Initializes External User Id description.
     *
     * @param userType external user type
     * @param userId   external user id
     * @throws IllegalArgumentException if external user type has a length less than 1 or greater than 10 characters,
     *                                  if external user id has a length less than 1 or greater than 40 characters.
     */
    ExternalUserId(String userType, String userId) {
        super();
        Preconditions.checkStringNotNullMaxLength(userType, "userType", 10);
        Preconditions.checkStringNotNullMaxLength(userId, "userId", 40);
        key = userType;
        value = userId;
    }
}
