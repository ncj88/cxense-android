package io.piano.android.cxense.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * User object.
 *
 */
@JsonClass(generateAdapter = true)
class User(
    type: String,
    id: String,
    @Json(name = "profile") val profiles: List<UserProfile>,
    @Json(name = "identities") val identities: List<UserIdentity>,
) : UserIdentity(type, id)
