# Piano DMP & Content SDK
![GitHub](https://img.shields.io/github/license/cXense/cxense-android)
![Maven Central](https://img.shields.io/maven-central/v/io.piano.android/cxense)
![GitHub Workflow Status (branch)](https://img.shields.io/github/actions/workflow/status/cXense/cxense-android/build.yml?branch=master)

## Migration from SDK 2.0-2.2 to 2.3+
1. Change dependency in your app script to `implementation("io.piano.android:cxense:VERSION")`
2. Remove Jitpack from your app script, if you don't have any other dependencies from this repository
3. Update all imports: `import com.cxense.cxensesdk.XXXXX` -> `import io.piano.android.cxense.XXXXX`

## Migration from SDK 1.X to 2.0
Since 2.0 SDK is written in Kotlin. Also we use [Timber](https://github.com/JakeWharton/timber) library for logging.

There are breaking changes in API:
* All methods `setXX(value)` in event's builders renamed to `XX(value)`. For example: `setEventId("id")` -> `eventId("id")`
* We changed parameters order `UserIdentity(id, type)` -> `UserIdentity(type, id)`
* We changed parameters order `cxenseSdk.getUserExternalData(id, type, callback)` -> `cxenseSdk.getUserExternalData(type, id, callback)`

## Installation guide
#### Requirements:

* Java 8 / Kotlin
* Android 5.0 (API level 21) or higher
* Gradle 6.0 or newer (use latest version if possible)

#### Setup:
Add in your app script:
Kotlin script
```
dependencies {
    ...
    implementation("io.piano.android:cxense:VERSION")
}
```
Groovy script
```
dependencies {
    ...
    implementation "io.piano.android:cxense:VERSION"
}
```

## Configure Piano DMP & Content SDK
Before SDK can be used in the application it require to be configured. Configuration can be easily provided by utilizing instance of special class called `CxenseConfiguration`. It contains set of methods vital for SDK's execution. It requires to set `CredentialsProvider`, which provides *username* and *API key* or *persistedId* (see [Pixel API for /dmp/push](https://docs.piano.io/dmp-api-dmp-push?paragraphId=657f46f37bda217)). You can provide it by using special set-method of 'CxenseConfiguration' class:
```kotlin
CxenseSdk.getInstance().configuration.apply {
    credentialsProvider = object : CredentialsProvider {
        override fun getUsername(): String = BuildConfig.USERNAME // load it from secured store

        override fun getApiKey(): String = BuildConfig.API_KEY // load it from secured store

        override fun getDmpPushPersistentId(): String = BuildConfig.PERSISTED_ID // fill if you want to use it instead username/api key
    }
}
```
Also you can create class, which implements CredentialsProvider and loads these values from any source (for example, from Firebase Remote config).
> SDK doesn't cache these values and ask CredentialsProvider at each request to API. It allows to change values dynamically.  
> User name and API key can be obtained from Cxense.com portal.  
> If DmpPushPersistentId is set, all performance events will be pushed through DMP pixel automatically. They are pushed through "/dmp/push/" API directly by default.  
> If you didn't specify username and API key, you can only send PageView/DMP events (with DmpPushPersistentId) and execute persisted queries via `executePersistedQuery` (with any other persisted id).

`Configuration` class provides control over other multiple options through which you can modify SDK's behaviour. All available for configuration options are listed below:
| Property | Required | Default value | Description |
| --- | --- | --- | --- |
| CredentialsProvider | Yes |  | Class, which provides: email of the user which has API access, API key of the user with API access or identifier of persistent query which points to "/dmp/push" API. |
| dispatchPeriod |  | 300 sec | Defines amount of seconds between tries which dispatch loop will perform in attempt to send reported events. Must be 10 seconds or more. |
| outdatePeriod |  | 7 days | Defines amount of seconds during which all dispatched events will be stored in local database after successful sending. Must be 10 minutes or more. |
| minimumNetworkStatus |  | none | Defines minimum network conditions upon which dispatch loop can send events. |
| dispatchMode |  | online | Defines dispatch mode of dispatch loop. |
| isAutoMetaInfoTrackingEnabled |  | true | Shows whether automatic meta-information tracking is enabled or not. Under meta-information following items are meant: app name, app version, sdk version. In case this flag is enabled, all specified parameters will be send as events' custom parameters. |
| consentSettings |  |  | Options that indicate consent on data processing. |
| sendEventsAtPush |  | false | Try to send all not submitted events at every `pushEvents` call (including passed as call parameters) |

To override default SDK's settings just set your values to any of these fields.
We recommend to put code related to SDK's configuration modification to 'onCreate()' method in custom application class.

## Events tracking
Piano DMP & Content SDK allows tracking events of these types:

* page view
* performance

*Page view event* is collection of data that describes the visit (time and length of visit; previous, current and next page URL; etc) and the visitor (browser, OS, location, IP address, etc). It is also reffered as 'traffic event'.
*Performance event* describes what the user did while visiting the page.

### Page View Events
Page view events are aggregated by Piano Insight. All collected page view events are available for analysis in Insight's web interface.

Use `PageViewEvent` class to track page view events in your application. Instances of the class can be easily created using following `PageViewEvent.Builder`:
```kotlin
// SiteId - identifier of the site for which current event will be reported
val builder = PageViewEvent.Builder("[put your SiteId here]")
```
`PageViewEventBuilder` class provide set of methods through which you can easily configure data that will be applied to page view instance. Here is example of event's configuration (pay attention that methods of the build can be chained):
```kotlin
builder.location("https://www.google.ru") // The URL of the page
        // User profile parameters can be set like this.
        // This method automatically prefixes specified key by required 'cp_u_' prefix
        .addCustomUserParameters(CustomParameter("xyz-favorite-song", "Hotel California"))
        // Custom parameters can be specified for event like this.
        // This method automatically prefixes specified key by required 'cp_' prefix
        .addCustomParameters(CustomParameter("xyz-user-timezone", "GMT0"))
```
Result instance of page view event can be created and scheduled to closest dispatch loop's iteration using following code:
```kotlin
CxenseSdk.getInstance().pushEvents(builder.build())
```
> Builder's `build()` method can throw `IllegalStateException` to indicate problems with provided parameters. More on error handling in "**Error handling**" part.

#### Setting user external ids

To specify current user external ids `PageViewEvent.Builder` class provides special function in its API - `addExternalUserId` Here how it can be used:
```kotlin
builder.addExternalUserIds(ExternalUserId("xyz", "qef4thyt"), ExternalUserId("xyz", "qaz1dfgh"))
```
> You can set up to five different external ids for current per event.

#### Track active time
 The SDK can track active time for page view events. For example - how long user had read specific article in the application. That can be easily done by using following function with event's name:
```kotlin
val event = builder.eventId("my-unique-id").build()
...
CxenseSdk.getInstance().trackActiveTime(event.eventId)
```
> This event id is used internally only as key

#### Track content (instead of pages, it is actual for OTT applications)
The SDK allows you tracking content views by leveraging page view events. If your content do not have own URL (streamed content for example), but can be identified through some alphanumerical string, than you can track it's views too. Just set your content id to `PageViewEvent.Builder` instance:
```kotlin
builder.contentId("897983476897356")
```
> `contentId` & `location` properties are mutually exclusive. Please use either content identifier or page's address.

### Performance Events
Performance events are events that means that user have performed some action in the application like 'Add item to the cart'. Performance events are aggregated by Piano DMP and all collected events of that type are available for analysis in DMP's web interface.

Use `PerformanceEvent` class to track performance events in your application. Instances of the class can be easily created using `PerformanceEvent.Builder`:
```kotlin
val builder = PerformanceEvent.Builder("[put your SiteId here]", "xyz-sample", "click", listOf(UserIdentity("xyz", "abcdefg12345")))
```
`PerformanceEvent.Builder` class provide set of methods through which you can easily configure data that will be applied on performance event. Here is example of event's configuration (pay attention that method's invocations can be chained):
```kotlin
// Custom parameters on performance events have different structure than custom parameters of page view events.
// They have more complex structure and can be configured through 'DMPCustomParameter' class usage.
builder.addCustomParameters(CustomParameter("val", "0.34"), CustomParameter("campaign", "ad"))
        // Identifier of segments are also can be provided to event's data.
        .addSegments("123", "456")
```
Also, reported page view events can be attached to performance events. That is how that can be implemented:
```kotlin
val event = ... // get instance of PageViewEvent that must be attached to PerformanceEvent
CxenseSdk.getInstance().pushEvents(event)
 
// while constructing new instance of PerformanceEvent using PerformanceEvent.Builder just use following method to associate pv event with perf event: 
...
.prnd(event.rnd)
... 
```
To schedule event's sending on closest dispatch loop's iteration, use following method for performance events too:
```kotlin
CxenseSdk.getInstance().pushEvents(...)
```
> Piano DMP & Content SDK automatically tracks meta information (like name and version) of application in which it is used by default. This will help you in understanding which version of the application had generated events and will help you in distinguishing traffic came from application from traffic came from web (if both platforms are supported in your product). Automatic tracking uses custom parameters to deliver meta information and can be disabled by setting to false following property of your `CxenseConfiguration` object: `CxenseSdk.getInstance().configuration.autoMetaInfoTrackingEnabled = false`

## Advanced scenarios
#### Using custom User Id
```kotlin
val cxenseSdk = CxenseSdk.getInstance()
 
// Get default user id. Current value is Advertising ID
val defaultUserId = cxenseSdk.defaultUserId
 
// Get current user id
val currentUserId = cxenseSdk.userId
 
// Set custom user id
cxenseSdk.userId = customUserId
```
#### Working with user consent (GDPR) 
If user consent is required in your application you should set `consentRequired` to enable checking consents before event processing. Add additional consent flags after requesting it from user. 
```kotlin
CxenseSdk.getInstance().configuration.apply {
    consentSettings
        .consentRequired(true)
        .pvAllowed(true)
        .segmentAllowed(true)
    consentSettings.version = 2 // required for deviceAllowed and geoAllowed 
}
```
> Pay attention that if given consent options may affect not only data processing on backend, but also affect SDK's functionality.

Possible consents (only if `consentRequired` is true):
| Option | Description |
| --- | --- |
| pvAllowed | Allows page view tracking, DMP event tracking and browsing habit collection to understand a user’s interests and profile. |
| recsAllowed | Allows personalisation of content recommendations and suggested content based on user interests and browsing habits |
| segmentAllowed | Allows audience segmentation, processing of browsing habits and first party data to include users in specific audience segments. |
| deviceAllowed | Covers any data, such as user-agent or browser information, that can be used to identify what device the user is using. If consent is not given, certain activities that require device information will be limited, since the data will not be present. |
| geoAllowed | Сovers looking up geolocation data on end-users. Practically, this covers geolocation via IP lookups to some precision. It also will reject the use of latitude and longitude parameters in page view requests. |
| adAllowed | Allows targeting advertising based on browsing habits and audience segmentation. |
#### Retrieve segments for a user
```kotlin
CxenseSdk.getInstance().getUserSegmentIds(listOf(UserIdentity(type, id), UserIdentity(type2, id2)), listOf(siteGroupId, siteGroupId2), object : LoadCallback<List<String>> {
    override fun onSuccess(data: List<String>) {
        // do something with data
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
```
#### Retrieve user profile
```kotlin
CxenseSdk.getInstance().getUser(UserIdentity(type, id), object : LoadCallback<User> {
    override fun onSuccess(data: User) {
        // do something with data
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
```
#### Retrieve content recommendations
Configure context for widget
```kotlin
// there is the only required parameter for a context: URL
val widgetContext = WidgetContext.Builder("YOUR_URL")
                // override other optional properties
                .referrer(...)
                .keywords(...)
                .neighbors(...)
                .parameters(...)
                .build()
```   
Fetch recommendations
```kotlin
CxenseSdk.getInstance().loadWidgetRecommendations(widgetId, widgetContext, callback = object : LoadCallback<List<WidgetItem>> {
    override fun onSuccess(data: List<WidgetItem>) {
        // work with loaded items
    }
    override fun onError(throwable: Throwable) {
        // process error
    }
})
```
Track item click
```kotlin
val cxenseSdk = CxenseSdk.getInstance()
// for item
cxenseSdk.trackClick(item, object: LoadCallback<Unit> {
    override fun onSuccess(data: Unit) {
        // success response from server
    }
    override fun onError(throwable: Throwable) {
        // process error
    }
})
// for custom url
cxenseSdk.trackClick(url, object: LoadCallback<Unit> {
    override fun onSuccess(data: Unit) {
        // success response from server
    }
    override fun onError(throwable: Throwable) {
        // process error
    }
})
```
#### Some debug methods
Get event sending queue status
```kotlin
CxenseSdk.getInstance().queueStatus
```
Set callback for each dispatch
```kotlin
CxenseSdk.getInstance().setDispatchEventsCallback { statuses ->
    statuses.filter { it.exception != null }.forEach {
        Log.e(
            TAG,
            String.format(Locale.getDefault(), "Error at sending event with id '%s'", it.eventId),
            it.exception
        )
    }
}
```
Force flush event queue
```kotlin
CxenseSdk.getInstance().flushEventQueue()
```
#### Working with external data
```kotlin
val id = "john.doe@example.com"
val type = "xyz"
val cxenseSdk = CxenseSdk.getInstance()
 
// read external data for all users with type
cxenseSdk.getUserExternalData(type, callback = object : LoadCallback<List<UserExternalData>> {
    override fun onSuccess(data: List<UserExternalData>) {
        // do something with data
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
 
// update external data for user
val userExternalData = UserExternalData.Builder(UserIdentity(type, id))
        .addExternalItems(ExternalItem("group1", "item1"), ExternalItem("group2", "item2"), ExternalItem("group3", "item3"))
        .build()
cxenseSdk.setUserExternalData(userExternalData, object : LoadCallback<Unit> {
    override fun onSuccess(data: Unit) {
        // do something at success (data not available)
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
 
// delete external data for user
cxenseSdk.deleteUserExternalData(UserIdentity(type, id), object : LoadCallback<Unit> {
    override fun onSuccess(data: Unit) {
        // do something at success (data not available)
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
```
> Updating external data will overwrite any existing data associated with the user. Hence, if the intention is to add a new key-value to the user without erasing the existing information, the client should first read the currently stored data, add the new data to the already stored data, and upload the new profile consisting of both the previously stored information,and the new information.

#### Using persisted queries
Piano DMP & Content SDK provides also support of [Persisted Queries](https://docs.piano.io/dmp-persisted-query/). It contains two methods in its public API that are work with the queries:
```kotlin
/**
 * Executes persisted query. You can find some popular endpoints in {@link CxenseConstants}
 *
 * @param url               API endpoint
 * @param persistentQueryId query id
 * @param data              data for sending as request body
 * @param callback          callback for response data
 * @param <T>               response type
 */
@JvmOverloads
fun <T : Any> executePersistedQuery(
    url: String,
    persistentQueryId: String,
    data: Any? = null,
    callback: LoadCallback<T>
)
```
It is generic API and you can use it with any API endpoint, that supports persisted queries,if you provide correct response object definition. SDK includes urls and definitions for popular API endpoints.

Sample usage:
We have persisted query for `/profile/user/segment` with request body `{"identities":[{"id":"0","type":"cx"}],"siteGroupIds":["1234567890123456789"]}` and mutable fields: `identities`
```kotlin
val data = UserSegmentRequest(listOf(UserIdentity(type, id)), null)
CxenseSdk.getInstance().executePersistedQuery(CxenseConstants.ENDPOINT_USER_SEGMENTS, "some_persistent_id", data, object : LoadCallback<SegmentsResponse> {
    override fun onSuccess(data: SegmentsResponse) {
        // do something with segmentsResponse.ids
    }
    override fun onError(throwable: Throwable) {
        // do something with error
    }
})
```
