package com.cxense.cxensesdk;

import java.util.concurrent.TimeUnit;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-11-23).
 */

public class CxenseConstants {
    /**
     * Default dispatch period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long DEFAULT_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(300);
    /**
     * Minimum dispatch period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long MIN_DISPATCH_PERIOD = TimeUnit.SECONDS.toMillis(10);
    /**
     * Default outdate period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long DEFAULT_OUTDATED_PERIOD = TimeUnit.DAYS.toMillis(7);
    /**
     * Minimum outdate period for events in milliseconds
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final long MIN_OUTDATE_PERIOD = TimeUnit.MINUTES.toMillis(10);

    /**
     * Endpoint for getting user segments
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_USER_SEGMENTS = "profile/user/segment";
    /**
     * Endpoint for getting user profile
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_USER_PROFILE = "profile/user";
    /**
     * Endpoint for getting user external data
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_READ_USER_EXTERNAL_DATA = "profile/user/external/read";
    /**
     * Endpoint for updating user external data
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_UPDATE_USER_EXTERNAL_DATA = "profile/user/external/update";
    /**
     * Endpoint for deleting user external data
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_DELETE_USER_EXTERNAL_DATA = "profile/user/external/delete";
    /**
     * Endpoint for getting user's external identity mapping
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_READ_USER_EXTERNAL_LINK = "profile/user/external/link";
    /**
     * Endpoint for creating new user's external identity mapping
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_UPDATE_USER_EXTERNAL_LINK = "profile/user/external/link/update";
    /**
     * Endpoint for pushing DMP events
     */
    @SuppressWarnings({"UnusedDeclaration", "WeakerAccess"}) // Public API.
    public static final String ENDPOINT_PUSH_DMP_EVENTS = "dmp/push";

    private CxenseConstants() {
    }
}
