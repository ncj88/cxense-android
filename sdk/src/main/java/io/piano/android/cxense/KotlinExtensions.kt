@file:JvmName("KotlinExtensions")

package io.piano.android.cxense

import io.piano.android.cxense.model.ContentUser
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserExternalData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.trackClick(item: WidgetItem) =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            item,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    continuation.resume(Unit)
                }

                override fun onError(throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }
            }
        )
    }

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.trackClick(url: String) =
    suspendCancellableCoroutine { continuation ->
        trackClick(
            url,
            object : LoadCallback<Unit> {
                override fun onSuccess(data: Unit) {
                    continuation.resume(Unit)
                }

                override fun onError(throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }
            }
        )
    }

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
suspend fun CxenseSdk.loadWidgetRecommendations(
    widgetId: String,
    widgetContext: WidgetContext? = null,
    user: ContentUser? = null,
    tag: String? = null,
    prnd: String? = null,
    experienceId: String? = null,
) = suspendCancellableCoroutine { continuation ->
    loadWidgetRecommendations(
        widgetId,
        widgetContext,
        user,
        tag,
        prnd,
        experienceId,
        object : LoadCallback<List<WidgetItem>> {
            override fun onSuccess(data: List<WidgetItem>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.reportWidgetVisibilities(
    vararg impressions: Impression,
) = suspendCancellableCoroutine { continuation ->
    reportWidgetVisibilities(
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        },
        *impressions
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.getUserSegmentIds(
    identities: List<UserIdentity>,
    siteGroupIds: List<String>,
) = suspendCancellableCoroutine { continuation ->
    getUserSegmentIds(
        identities,
        siteGroupIds,
        object : LoadCallback<List<String>> {
            override fun onSuccess(data: List<String>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
suspend fun CxenseSdk.getUser(
    identity: UserIdentity,
    groups: List<String>? = null,
    recent: Boolean? = null,
    identityTypes: List<String>? = null,
) = suspendCancellableCoroutine { continuation ->
    getUser(
        identity,
        groups,
        recent,
        identityTypes,
        object : LoadCallback<User> {
            override fun onSuccess(data: User) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
suspend fun CxenseSdk.getUserExternalData(
    type: String,
    id: String? = null,
    filter: String? = null,
) = suspendCancellableCoroutine { continuation ->
    getUserExternalData(
        type,
        id,
        filter,
        object : LoadCallback<List<UserExternalData>> {
            override fun onSuccess(data: List<UserExternalData>) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.setUserExternalData(
    userExternalData: UserExternalData,
) = suspendCancellableCoroutine { continuation ->
    setUserExternalData(
        userExternalData,
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.deleteUserExternalData(
    identity: UserIdentity,
) = suspendCancellableCoroutine { continuation ->
    deleteUserExternalData(
        identity,
        object : LoadCallback<Unit> {
            override fun onSuccess(data: Unit) {
                continuation.resume(Unit)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.getUserExternalLink(
    cxenseId: String,
    type: String,
) = suspendCancellableCoroutine { continuation ->
    getUserExternalLink(
        cxenseId,
        type,
        object : LoadCallback<UserIdentity> {
            override fun onSuccess(data: UserIdentity) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
suspend fun CxenseSdk.addUserExternalLink(
    cxenseId: String,
    identity: UserIdentity,
) = suspendCancellableCoroutine { continuation ->
    addUserExternalLink(
        cxenseId,
        identity,
        object : LoadCallback<UserIdentity> {
            override fun onSuccess(data: UserIdentity) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}

@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
@JvmOverloads
suspend inline fun <reified T : Any> CxenseSdk.executePersistedQuery(
    url: String,
    persistentQueryId: String,
    data: Any? = null,
) = suspendCancellableCoroutine { continuation ->
    executePersistedQuery(
        url,
        persistentQueryId,
        data,
        object : LoadCallback<T> {
            override fun onSuccess(data: T) {
                continuation.resume(data)
            }

            override fun onError(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        }
    )
}
