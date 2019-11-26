package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.ConsentOption
import com.cxense.cxensesdk.model.ContentUser
import com.cxense.cxensesdk.model.Event
import com.cxense.cxensesdk.model.Impression
import com.cxense.cxensesdk.model.QueueStatus
import com.cxense.cxensesdk.model.User
import com.cxense.cxensesdk.model.UserDataRequest
import com.cxense.cxensesdk.model.UserExternalData
import com.cxense.cxensesdk.model.UserExternalDataRequest
import com.cxense.cxensesdk.model.UserIdentity
import com.cxense.cxensesdk.model.UserIdentityMappingRequest
import com.cxense.cxensesdk.model.UserSegmentRequest
import com.cxense.cxensesdk.model.WidgetContext
import com.cxense.cxensesdk.model.WidgetItem
import com.cxense.cxensesdk.model.WidgetRequest
import com.cxense.cxensesdk.model.WidgetVisibilityReport
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Singleton class used as a facade to the Cxense services
 * Read full documentation <a href="https://wiki.cxense.com/display/cust/Cxense+SDK+for+Android">here</a>
 *
 */
class CxenseSdk(
    private val executor: ScheduledExecutorService,
    @Suppress("unused") // Public API.
    val configuration: CxenseConfiguration,
    private val advertisingIdProvider: AdvertisingIdProvider,
    private val userProvider: UserProvider,
    private val cxApi: CxApi,
    private val errorParser: ApiErrorParser,
    private val gson: Gson,
    private val eventRepository: EventRepository,
    private val sendTask: SendTask
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
    @Suppress("unused") // Public API.
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
    @Suppress("unused") // Public API.
    val defaultUserId: String?
        get() = advertisingIdProvider.defaultUserId

    /**
     * Retrieves whether the user has limit ad tracking enabled or not.
     */
    @Suppress("unused") // Public API.
    val limitAdTrackingEnabled: Boolean
        get() = advertisingIdProvider.limitAdTrackingEnabled

    ////////// Work with events
    /**
     * Sets callback for each dispatching of events
     *
     * @param callback callback instance
     */
    @Suppress("unused") // Public API.
    fun setDispatchEventsCallback(callback: DispatchEventsCallback?) {
        sendTask.sendCallback = callback
    }

    /**
     * Push events to sending queue.
     *
     * @param events the events that should be pushed.
     */
    @Suppress("unused") // Public API.
    fun pushEvents(vararg events: Event) =
        executor.execute { eventRepository.putEventsInDatabase(events) }

    /**
     * Tracks active time for the given page view event.
     *
     * @param eventId    the event to report active time for.
     * @param activeTime the active time in seconds.
     */
    @Suppress("unused") // Public API.
    fun trackActiveTime(eventId: String, activeTime: Long = 0) =
        executor.execute { eventRepository.putEventTime(eventId, activeTime) }

    /**
     * Forces sending events from queue to server.
     */
    @Suppress("unused") // Public API.
    fun flushEventQueue() = executor.execute(sendTask)

    /**
     * Returns current event queue status
     *
     * @return queue status
     */
    @Suppress("unused") // Public API.
    fun getQueueStatus(): QueueStatus =
        QueueStatus(eventRepository.getEventStatuses())

    ////////// Content API
    @Suppress("unused") // Public API.
    val defaultContentUser: ContentUser
        get() = userProvider.defaultUser

    /**
     * Tracks an url click for the given item
     *
     * @param item     the item that contains the click-url
     * @param callback callback for checking status
     */
    @Suppress("unused") // Public API.
    fun trackClick(item: WidgetItem, callback: LoadCallback<Void>) =
        item.clickUrl?.let { trackClick(it, callback) }
            ?: callback.onError(BaseException("Can't track this item. Click url is null"))

    /**
     * Tracks a click for the given click-url
     *
     * @param url      the click-url
     * @param callback callback for checking status
     */
    @Suppress("unused") // Public API.
    fun trackClick(url: String, callback: LoadCallback<Void>) = cxApi.trackUrlClick(url).enqueue(callback)

    /**
     * Load widget recommendations
     *
     * @param widgetId      the widget id
     * @param widgetContext the WidgetContext
     * @param user          custom user
     * @param tag           Only display results from the branch with the given tag and ignore other conditions.
     * @param prnd          Identifier for the page view where the result of this call will be displayed.
     * @param callback      listener for returning result
     */
    @Suppress("unused") // Public API.
    fun loadWidgetRecommendations(
        widgetId: String,
        widgetContext: WidgetContext? = null,
        user: ContentUser? = null,
        tag: String? = null,
        prnd: String? = null,
        callback: LoadCallback<List<WidgetItem>>
    ) = cxApi.getWidgetData(
        WidgetRequest(
            widgetId,
            configuration.consentOptionsValues,
            widgetContext,
            user ?: defaultContentUser,
            tag,
            prnd
        )
    ).enqueue(callback) { it.items }


    /**
     * Report visibility of recommendations for a content widget.
     *
     * @param impressions The list of seen recommendations (impressions) you'd like to report visibility of.
     */
    @Suppress("unused") // Public API.
    fun reportWidgetVisibilities(callback: LoadCallback<Void>, vararg impressions: Impression) =
        cxApi.reportWidgetVisibility(
            WidgetVisibilityReport(impressions.toList())
        ).enqueue(callback)

    ////////// DMP API
    /**
     * Asynchronously retrieves a list of all segments where the specified user is a member
     *
     * @param identities   a list of user identifiers for a single user to retrieve segments for
     * @param siteGroupIds the list of site groups to retrieve segments for
     * @param callback     a  callback to receive a list of segment identifiers where the specified user is a member
     */
    @Suppress("unused") // Public API.
    fun getUserSegmentIds(
        identities: List<UserIdentity>,
        siteGroupIds: List<String>,
        callback: LoadCallback<List<String>>
    ) {
        val consentOptions = configuration.consentOptions
        if (ConsentOption.CONSENT_REQUIRED in consentOptions && ConsentOption.SEGMENT_ALLOWED !in consentOptions) {
            callback.onError(ConsentRequiredException())
            return
        }
        cxApi.getUserSegments(UserSegmentRequest(identities, siteGroupIds)).enqueue(callback) { it.ids }
    }

    /**
     * Asynchronously retrieves a suitably authorized slice of a given user's interest profile
     *
     * @param identity      user identifier with type and id
     * @param groups        a collection of strings that specify profile item groups to keep in the returned profile.
     *                      If not specified, all groups available for the user will be returned
     * @param recent        flag whether to only return the most recent user profile information. This can be used to
     *                      return quickly if response time is important
     * @param identityTypes a collection of external customer identifier types. If an external customer identifier exists for
     *                      the user, it will be included in the response
     * @param callback      a callback with {@link User} profile
     */
    @Suppress("unused") // Public API.
    fun getUser(
        identity: UserIdentity,
        groups: List<String>? = null,
        recent: Boolean? = null,
        identityTypes: List<String>? = null,
        callback: LoadCallback<User>
    ) = cxApi.getUser(UserDataRequest(identity, groups, recent, identityTypes)).enqueue(callback)

    /**
     * Asynchronously retrieves the external data associated with a given user
     *
     * @param id       identifier for the user. Use 'null' if you want match all users of provided type.
     * @param type     the customer identifier type
     * @param filter   a traffic filter of type user-external with required group and optional item/items specified
     * @param callback a callback with {@link UserExternalData}
     */
    @Suppress("unused") // Public API.
    fun getUserExternalData(
        type: String,
        id: String? = null,
        filter: String? = null,
        callback: LoadCallback<List<UserExternalData>>
    ) = cxApi.getUserExternalData(UserExternalDataRequest(type, id, filter)).enqueue(callback) { it.items }

    /**
     * Asynchronously sets the external data associated with a given user
     *
     * @param userExternalData external data associated with a user
     * @param callback         a callback
     */
    @Suppress("unused") // Public API.
    fun setUserExternalData(userExternalData: UserExternalData, callback: LoadCallback<Void>) =
        cxApi.setUserExternalData(userExternalData).enqueue(callback)

    /**
     * Asynchronously deletes the external data associated with a given user
     *
     * @param identity user identifier with type and id
     * @param callback a callback
     */
    @Suppress("unused") // Public API.
    fun deleteUserExternalData(userIdentity: UserIdentity, callback: LoadCallback<Void>) =
        cxApi.deleteExternalUserData(userIdentity).enqueue(callback)

    /**
     * Asynchronously retrieves a registered external identity mapping for a Cxense identifier
     *
     * @param cxenseId the Cxense identifier of the user.
     * @param type     the identity mapping type (customer identifier type) that contains the mapping.
     * @param callback a callback with {@link UserIdentity}
     */
    @Suppress("unused") // Public API.
    fun getUserExternalLink(cxenseId: String, type: String, callback: LoadCallback<UserIdentity>) =
        cxApi.getUserExternalLink(UserIdentityMappingRequest(cxenseId, type)).enqueue(callback)

    /**
     * Asynchronously register a new identity-mapping for the given user
     *
     * @param cxenseId the Cxense identifier to map this user to
     * @param identity user identifier with type and id
     * @param callback a callback with {@link UserIdentity}
     */
    @Suppress("unused") // Public API.
    fun addUserExternalLink(cxenseId: String, identity: UserIdentity, callback: LoadCallback<UserIdentity>) =
        cxApi.addUserExternalLink(UserIdentityMappingRequest(cxenseId, identity.type, identity.id)).enqueue(callback)

    ////////// Persisted API
    @Suppress("unused") // Public API.
    fun <T> executePersistedQuery(
        url: String,
        persistentQueryId: String,
        data: Any? = null,
        callback: LoadCallback<T>
    ) {
        with(cxApi) {
            data?.let { postPersisted(url, persistentQueryId, it) } ?: getPersisted(url, persistentQueryId)
        }.enqueue(createGenericCallback(callback))
    }

    ////////// Internal methods
    private fun <T> Call<T>.enqueue(callback: LoadCallback<T>) = enqueue(callback.transform())

    private fun <T, U> Call<T>.enqueue(callback: LoadCallback<U>, function: (T) -> U) =
        enqueue(callback.transform(function))

    private fun <T, U> LoadCallback<U>.transform(function: (T) -> U) = object : LoadCallback<T> {
        override fun onSuccess(data: T) = this@transform.onSuccess(function(data))

        override fun onError(throwable: Throwable) = this@transform.onError(throwable)

    }.transform()

    private fun <T> LoadCallback<T>.transform() = ApiCallback(this, errorParser)

    private fun <T> createGenericCallback(callback: LoadCallback<T>) = object : LoadCallback<ResponseBody> {
        override fun onSuccess(data: ResponseBody) =
            try {
                @Suppress("UNCHECKED_CAST")
                val clazz = (callback::class.java
                    .genericInterfaces
                    .first() as ParameterizedType)
                    .actualTypeArguments
                    .first() as Class<T>
                callback.onSuccess(gson.fromJson(data.charStream(), clazz))
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
        @JvmStatic
        @Suppress("unused")
        fun getInstance(): CxenseSdk = DependenciesProvider.getInstance().cxenseSdk

        private val DISPATCH_INITIAL_DELAY = TimeUnit.SECONDS.toMillis(10)
    }
}
