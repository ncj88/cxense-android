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
     *                                  if external user id has a length less than 1 or greater than 64 characters.
     */
    ExternalUserId(String userType, String userId) {
        super();
        Preconditions.checkStringNotNullMaxLength(userType, "userType", 10);
        Preconditions.checkStringForRegex(userId, "userId", "^[\\w =@+-\\.]{1,64}$", "The valid characters are digits, letters, space and the special characters \"=\", \"@\", \"+\", \"-\", \"_\" and \".\". Max length is 64 symbols");
        key = userType;
        value = userId;
    }
}
