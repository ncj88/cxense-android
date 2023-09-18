package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request object for getting user from server
 *
 */
@JsonClass(generateAdapter = true)
class UserDataRequest(
    type: String,
    id: String,
    @Json(name = "groups") val groups: List<String>? = null,
    @Json(name = "recent") val recent: Boolean? = null,
    @Json(name = "identityTypes") val identityTypes: List<String>? = null,
) : UserIdentity(type, id) {
    constructor(
        userIdentity: UserIdentity,
        @Json(name = "groups") groups: List<String>? = null,
        @Json(name = "recent") recent: Boolean? = null,
        @Json(name = "identityTypes") identityTypes: List<String>? = null,
    ) : this(userIdentity.type, userIdentity.id, groups, recent, identityTypes)
}
