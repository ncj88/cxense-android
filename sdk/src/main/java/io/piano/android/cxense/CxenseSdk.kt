package io.piano.android.cxense

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.piano.android.cxense.model.CandidateSegment
import io.piano.android.cxense.model.ContentUser
import io.piano.android.cxense.model.Event
import io.piano.android.cxense.model.EventStatus
import io.piano.android.cxense.model.Impression
import io.piano.android.cxense.model.QueueStatus
import io.piano.android.cxense.model.Segment
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserDataRequest
import io.piano.android.cxense.model.UserExternalData
import io.piano.android.cxense.model.UserExternalDataRequest
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserIdentityMappingRequest
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetContext
import io.piano.android.cxense.model.WidgetItem
import io.piano.android.cxense.model.WidgetRequest
import io.piano.android.cxense.model.WidgetVisibilityReport
import okhttp3.ResponseBody
import retrofit2.Call
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 * @property configuration Cxense SDK configuration, see [CxenseConfiguration]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
class CxenseSdk(
    private val executor: ScheduledExecutorService,
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    val configuration: CxenseConfiguration,
    private val advertisingIdProvider: AdvertisingIdProvider,
    private val userProvider: UserProvider,
    private val cxApi: CxApi,
    private val errorParser: ApiErrorParser,
    private val moshi: Moshi,
    private val eventRepository: EventRepository,
    private val sendTask: SendTask,
) {
    init {
        configuration.dispatchPeriodListener = { initSendTaskSchedule() }
        initSendTaskSchedule()
    }

    private var scheduled: ScheduledFuture<*>? = null

    /**
     * The user id used by this SDK. Must be at least 16 characters long.
     * Allowed characters are: A-Z, a-z, 0-9, "_", "-", "+" and ".".
     *
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    var userId: String
        get() = userProvider.userId
        set(value) {
            userProvider.userId = value
        }

    /**
     * Retrieves the default user id for SDK.
     *
     * @return advertising ID (if available)
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    val defaultUserId: String?
        get() = advertisingIdProvider.defaultUserId

    /**
     * Retrieves whether the user has limit ad tracking enabled or not.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    val limitAdTrackingEnabled: Boolean
        get() = advertisingIdProvider.limitAdTrackingEnabled

    // -------- Work with events
    /**
     * Sets callback for each dispatching of events
     *
     * @param callback callback instance
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun setDispatchEventsCallback(callback: DispatchEventsCallback?) {
        sendTask.sendCallback = callback
    }

    /**
     * Push events to sending queue.
     *
     * @param events the events that should be pushed.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun pushEvents(vararg events: Event) =
        executor.execute {
            eventRepository.putEventsInDatabase(events)
            if (configuration.sendEventsAtPush) {
                flushEventQueue()
            }
        }

    /**
     * Tracks active time for the given page view event.
     *
     * @param eventId the event to report active time for.
     * @param activeTime the active time in seconds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun trackActiveTime(eventId: String, activeTime: Long = 0) =
        executor.execute { eventRepository.putEventTime(eventId, activeTime) }

    /**
     * Forces sending events from queue to server.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun flushEventQueue() = executor.execute(sendTask)

    /**
     * Returns current event queue status
     *
     * @return queue status
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    val queueStatus: QueueStatus
        get() = QueueStatus(eventRepository.getEventStatuses())

    // -------- Content API
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    val defaultContentUser: ContentUser
        get() = userProvider.defaultUser

    /**
     * Tracks an url click for the given item
     *
     * @param item the item that contains the click-url
     * @param callback callback for checking status
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun trackClick(item: WidgetItem, callback: LoadCallback<@JvmSuppressWildcards Unit>) =
        item.clickUrl?.let { trackClick(it, callback) }
            ?: callback.onError(BaseException("Can't track this item. Click url is null"))

    /**
     * Tracks a click for the given click-url
     *
     * @param url the click-url
     * @param callback callback for checking status
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun trackClick(url: String, callback: LoadCallback<@JvmSuppressWildcards Unit>) =
        cxApi.trackUrlClick(url).enqueue(callback)

    /**
     * Load widget recommendations
     *
     * @param widgetId the widget id
     * @param widgetContext the WidgetContext
     * @param user custom user
     * @param tag Only display results from the branch with the given tag and ignore other conditions.
     * @param prnd Identifier for the page view where the result of this call will be displayed.
     * @param experienceId Experience identifier for C1X
     * @param callback listener for returning result
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun loadWidgetRecommendations(
        widgetId: String,
        widgetContext: WidgetContext? = null,
        user: ContentUser? = null,
        tag: String? = null,
        prnd: String? = null,
        experienceId: String? = null,
        callback: LoadCallback<List<WidgetItem>>,
    ) = cxApi.getWidgetData(
        WidgetRequest(
            widgetId,
            configuration.consentSettings.consents,
            widgetContext,
            user ?: defaultContentUser,
            tag,
            prnd,
            experienceId
        )
    ).enqueue(callback) { it.items }

    /**
     * Report visibility of recommendations for a content widget.
     *
     * @param impressions The list of seen recommendations (impressions) you'd like to report visibility of.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun reportWidgetVisibilities(callback: LoadCallback<@JvmSuppressWildcards Unit>, vararg impressions: Impression) =
        cxApi.reportWidgetVisibility(
            WidgetVisibilityReport(impressions.toList())
        ).enqueue(callback)

    // -------- DMP API
    /**
     * Asynchronously retrieves a list of all segments where the specified user is a member
     *
     * @param identities a list of user identifiers for a single user to retrieve segments for
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param candidateSegments A list of candidate segments to consider. The response segment matches will be a subset of these candidates.
     * @param segmentFormat The segment format, one of [UserSegmentRequest.SegmentFormat.STANDARD] (the standard segment ID format that can be found in the platform)
     *                      or [UserSegmentRequest.SegmentFormat.SHORT_IDS] (a specially generated shortened version of the segment identifier compatible with some Ad servers).
     * @param callback a  callback to receive a list of segment identifiers where the specified user is a member
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun getUserSegments(
        identities: List<UserIdentity>,
        siteGroupIds: List<String>,
        candidateSegments: List<CandidateSegment>? = null,
        segmentFormat: UserSegmentRequest.SegmentFormat = UserSegmentRequest.SegmentFormat.STANDARD,
        callback: LoadCallback<List<Segment>>,
    ) {
        require(identities.isNotEmpty()) {
            "You should provide at least one user identity"
        }
        val siteGroups = siteGroupIds
            .filterNot { it.isEmpty() }
            .also {
                require(it.isNotEmpty()) {
                    "You should provide at least one not empty site group id"
                }
            }
        val segmentsDenied = with(configuration.consentSettings) {
            consentRequired && !segmentAllowed
        }
        if (segmentsDenied) {
            callback.onError(ConsentRequiredException())
            return
        }
        cxApi.getUserTypedSegments(
            UserSegmentRequest(
                identities,
                siteGroups,
                candidateSegments,
                UserSegmentRequest.ResponseFormat.CX_TYPED,
                segmentFormat
            )
        ).enqueue(callback) { it.segments }
    }

    /**
     * Asynchronously retrieves a list of all segments where the specified user is a member
     *
     * @param identities a list of user identifiers for a single user to retrieve segments for
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param callback a  callback to receive a list of segment identifiers where the specified user is a member
     */
    @Deprecated("Use `getUserSegments`")
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun getUserSegmentIds(
        identities: List<UserIdentity>,
        siteGroupIds: List<String>,
        callback: LoadCallback<List<String>>,
    ) {
        require(identities.isNotEmpty()) {
            "You should provide at least one user identity"
        }
        val siteGroups = siteGroupIds
            .filterNot { it.isEmpty() }
            .also {
                require(it.isNotEmpty()) {
                    "You should provide at least one not empty site group id"
                }
            }
        val segmentsDenied = with(configuration.consentSettings) {
            consentRequired && !segmentAllowed
        }
        if (segmentsDenied) {
            callback.onError(ConsentRequiredException())
            return
        }
        cxApi.getUserSegments(UserSegmentRequest(identities, siteGroups)).enqueue(callback) { it.ids }
    }

    /**
     * Asynchronously retrieves a suitably authorized slice of a given user's interest profile
     *
     * @param identity user identifier with type and id
     * @param groups a collection of strings that specify profile item groups to keep in the returned profile. If not specified, all groups available for the user will be returned
     * @param recent flag whether to only return the most recent user profile information. This can be used to return quickly if response time is important
     * @param identityTypes a collection of external customer identifier types. If an external customer identifier exists for the user, it will be included in the response
     * @param callback a callback with {@link User} profile
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun getUser(
        identity: UserIdentity,
        groups: List<String>? = null,
        recent: Boolean? = null,
        identityTypes: List<String>? = null,
        callback: LoadCallback<User>,
    ) = cxApi.getUser(UserDataRequest(identity, groups, recent, identityTypes)).enqueue(callback)

    /**
     * Asynchronously retrieves the external data associated with a given user
     *
     * @param id identifier for the user. Use 'null' if you want match all users of provided type.
     * @param type the customer identifier type
     * @param filter a traffic filter of type user-external with required group and optional item/items specified
     * @param groups a list of group names. The result will show a subset of profiles that include these groups.
     * @param callback a callback with {@link UserExternalData}
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun getUserExternalTypedData(
        type: String,
        id: String? = null,
        filter: String? = null,
        groups: List<String>? = null,
        callback: LoadCallback<List<@JvmSuppressWildcards UserExternalTypedData>>,
    ) = cxApi.getUserExternalTypedData(
        UserExternalDataRequest(type, id, filter, groups, format = UserExternalDataRequest.ResponseFormat.TYPED)
    ).enqueue(callback) { it.items }

    /**
     * Asynchronously retrieves the external data associated with a given user
     *
     * @param id identifier for the user. Use 'null' if you want match all users of provided type.
     * @param type the customer identifier type
     * @param filter a traffic filter of type user-external with required group and optional item/items specified
     * @param callback a callback with {@link UserExternalData}
     */
    @Deprecated("Use `getUserExternalTypedData`")
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun getUserExternalData(
        type: String,
        id: String? = null,
        filter: String? = null,
        callback: LoadCallback<List<@JvmSuppressWildcards UserExternalData>>,
    ) = cxApi.getUserExternalData(UserExternalDataRequest(type, id, filter, null)).enqueue(callback) { it.items }

    /**
     * Asynchronously sets the external data associated with a given user
     *
     * @param userExternalData external data associated with a user
     * @param callback a callback
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun setUserExternalTypedData(
        userExternalData: UserExternalTypedData,
        callback: LoadCallback<@JvmSuppressWildcards Unit>,
    ) = cxApi.setUserExternalTypedData(userExternalData).enqueue(callback)

    /**
     * Asynchronously sets the external data associated with a given user
     *
     * @param userExternalData external data associated with a user
     * @param callback a callback
     */
    @Deprecated("Use `setUserExternalTypedData`")
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun setUserExternalData(userExternalData: UserExternalData, callback: LoadCallback<@JvmSuppressWildcards Unit>) =
        cxApi.setUserExternalData(userExternalData).enqueue(callback)

    /**
     * Asynchronously deletes the external data associated with a given user
     *
     * @param identity user identifier with type and id
     * @param callback a callback
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun deleteUserExternalData(identity: UserIdentity, callback: LoadCallback<@JvmSuppressWildcards Unit>) =
        cxApi.deleteExternalUserData(identity).enqueue(callback)

    /**
     * Asynchronously retrieves a registered external identity mapping for a Cxense identifier
     *
     * @param cxenseId the Cxense identifier of the user.
     * @param type the identity mapping type (customer identifier type) that contains the mapping.
     * @param callback a callback with {@link UserIdentity}
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun getUserExternalLink(cxenseId: String, type: String, callback: LoadCallback<UserIdentity>) =
        cxApi.getUserExternalLink(UserIdentityMappingRequest(cxenseId, type)).enqueue(callback)

    /**
     * Asynchronously register a new identity-mapping for the given user
     *
     * @param cxenseId the Cxense identifier to map this user to
     * @param identity user identifier with type and id
     * @param callback a callback with {@link UserIdentity}
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    fun addUserExternalLink(cxenseId: String, identity: UserIdentity, callback: LoadCallback<UserIdentity>) =
        cxApi.addUserExternalLink(UserIdentityMappingRequest(cxenseId, identity.type, identity.id)).enqueue(callback)

    // -------- Persisted API
    /**
     * Executes persisted query. You can find some popular endpoints in {@link CxenseConstants}
     *
     * @param url               API endpoint
     * @param persistentQueryId query id
     * @param data              data for sending as request body
     * @param callback          callback for response data
     * @param <T>               response type
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate") // Public API.
    @JvmOverloads
    fun <T : Any> executePersistedQuery(
        url: String,
        persistentQueryId: String,
        data: Any? = null,
        callback: LoadCallback<T>,
    ) {
        with(cxApi) {
            data?.let { postPersisted(url, persistentQueryId, it) } ?: getPersisted(url, persistentQueryId)
        }.enqueue(createGenericCallback(callback))
    }

    // -------- Internal methods
    private fun <T : Any> Call<T>.enqueue(callback: LoadCallback<T>) = enqueue(callback.transform())

    private fun <T : Any, U : Any> Call<T>.enqueue(callback: LoadCallback<U>, function: (T) -> U) =
        enqueue(callback.transform(function))

    private fun <T : Any, U : Any> LoadCallback<U>.transform(function: (T) -> U) = object : LoadCallback<T> {
        override fun onSuccess(data: T) = this@transform.onSuccess(function(data))

        override fun onError(throwable: Throwable) = this@transform.onError(throwable)
    }.transform()

    private fun <T : Any> LoadCallback<T>.transform() = ApiCallback(this, errorParser)

    private fun <T : Any> createGenericCallback(callback: LoadCallback<T>) = object : LoadCallback<ResponseBody> {
        override fun onSuccess(data: ResponseBody) =
            try {
                val callbackClazz = callback::class.java.genericInterfaces.first() as ParameterizedType

                @Suppress("UNCHECKED_CAST")
                val clazz = callbackClazz.actualTypeArguments.first() as Class<T>
                val jsonAdapter = moshi.adapter(clazz)
                val reader = JsonReader.of(data.source())
                callback.onSuccess(requireNotNull(jsonAdapter.fromJson(reader)))
            } catch (e: Exception) {
                callback.onError(e)
            }

        override fun onError(throwable: Throwable) = callback.onError(throwable)
    }.transform()

    private fun initSendTaskSchedule() {
        scheduled?.cancel(false)
        scheduled = executor.scheduleWithFixedDelay(
            sendTask,
            DISPATCH_INITIAL_DELAY,
            configuration.dispatchPeriod,
            TimeUnit.MILLISECONDS
        )
    }

    companion object {
        /**
         * Gets singleton SDK instance.
         */
        @JvmStatic
        @Suppress("unused")
        fun getInstance(): CxenseSdk = DependenciesProvider.getInstance().cxenseSdk

        @JvmStatic
        fun contentUrl(siteId: String, contentId: String) = "https://$siteId.content.id/$contentId"

        private val DISPATCH_INITIAL_DELAY = TimeUnit.SECONDS.toMillis(10)
    }

    fun interface DispatchEventsCallback {
        fun onDispatch(statuses: List<EventStatus>)
    }
}
