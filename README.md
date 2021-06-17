[![Release](https://jitpack.io/v/com.cxpublic/cxense-android.svg)](https://jitpack.io/#com.cxpublic/cxense-android) 
> You can find sample in Java and Kotlin at [Github repo](https://github.com/cXense/cxense-android-sample).

## Migration from SDK 1.X to 2.0
Since 2.0 SDK is written in Kotlin. Also we use [Timber](https://github.com/JakeWharton/timber) library for logging.

There are breaking changes in API:
* All methods `setXX(value)` in event's builders renamed to `XX(value)`. For example: `setEventId("id")` -> `eventId("id")`
* We changed parameters order `UserIdentity(id, type)` -> `UserIdentity(type, id)`
* We changed parameters order `cxenseSdk.getUserExternalData(id, type, callback)` -> `cxenseSdk.getUserExternalData(type, id, callback)`

## Installation guide
#### Requirements:

* Java 8 / Kotlin
* Android 4.0.3 (API level 15) or higher
* Gradle 5.0 or newer (use latest version if possible)

#### Steps:

1. Setup JitPack repository
Add in your root gradle script at the end of repositories:
Groovy script

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Kotlin script
```
allprojects {
    repositories {
        ...
        maven("https://jitpack.io")
    }
}
```
2. Setup Cxense SDK
Add in your app script:
Groovy script
```
android {
    ...
    packagingOptions {
        exclude 'META-INF/LICENSE'
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    ...
    implementation "com.cxpublic:cxense-android:VERSION"
}
```
Kotlin script
```
android {
    ...
    packagingOptions {
        exclude("META-INF/LICENSE")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    ...
    implementation("com.cxpublic:cxense-android:VERSION")
}
```

## Configure Cxense SDK
Before Cxense SDK can be used in the application it require to be configured. Configuration can be easily provided by utilizing instance of special class called `CxenseConfiguration`. It contains set of methods vital for SDK's execution. It requires to set `CredentialsProvider`, which provides *username* and *API key* or *persistedId* (see Pixel API for /dmp/push). You can provide it by using special set-method of 'CxenseConfiguration' class:
```java
CxenseConfiguration config = CxenseSdk.getInstance().getConfiguration();
config.setCredentialsProvider(new CredentialsProvider() {
    @Override
    public String getUsername() {
        return BuildConfig.USERNAME; // load it from secured storage
    }
    @Override
    public String getApiKey() {
        return BuildConfig.API_KEY; // load it from secured storage
    }
});
```
or
```java
CxenseConfiguration config = CxenseSdk.getInstance().getConfiguration();
config.setCredentialsProvider(new CredentialsProvider() {
    @Override
    public String getDmpPushPersistentId() {
        return BuildConfig.PERSISTED_ID;  // load it from storage
    }
});
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
| consentOptions |  |  | List of options that indicate consent on data processing. |

To override default SDK's settings just set your values to any of these fields.
We recommend to put code related to SDK's configuration modification to 'onCreate()' method in custom application class.

## Events tracking
Cxense SDK allows tracking events of these types:

* page view
* performance
* conversion

*Page view event* is collection of data that describes the visit (time and length of visit; previous, current and next page URL; etc) and the visitor (browser, OS, location, IP address, etc). It is also reffered as 'traffic event'.
*Performance event* describes what the user did while visiting the page.
*Conversion event* used for CCE support.

### Page View Events
Page view events are aggregated by Cxense Insight. All collected page view events are available for analysis in Insight's web interface.

Use `PageViewEvent` class to track page view events in your application. Instances of the class can be easily created using following `PageViewEvent.Builder`:
```java
// SiteId - identifier of the site for which current event will be reported
PageViewEvent.Builder builder = new PageViewEvent.Builder("[put your SiteId here]");
```
`PageViewEventBuilder` class provide set of methods through which you can easily configure data that will be applied to page view instance. Here is example of event's configuration (pay attention that methods of the build can be chained):
```java
builder.setLocation("https://www.google.ru") // The URL of the page
        // User profile parameters can be set like this.
        // This method automatically prefixes specified key by required 'cp_u_' prefix
        .addCustomUserParameter("xyz-favorite-song", "Hotel California")
        // Custom parameters can be specified for event like this.
        // This method automatically prefixes specified key by required 'cp_' prefix
        .addCustomParameter("xyz-user-timezone", "GMT0");
```
Result instance of page view event can be created and scheduled to closest dispatch loop's iteration using following code:
```java
CxenseSdk.getInstance().pushEvents(builder.build());
```
> Builder's `build()` method can throw `IllegalStateException` to indicate problems with provided parameters. More on error handling in "**Error handling**" part.

#### Setting user external ids

To specify current user external ids `PageViewEvent.Builder` class provides special function in its API - `addExternalUserId` Here how it can be used:
```java
// Add one id
builder.addExternalUserId("xyz", "qef4thyt");
// Or many at once
builder.addExternalUserIds(new ExternalUserId("xyz", "qef4thyt"), new ExternalUserId("xyz", "qaz1dfgh"));
```
> You can set up to five different external ids for current per event.

#### Track active time
 The SDK can track active time for page view events. For example - how long user had read specific article in the application. That can be easily done by using following function with event's name:
```java
PageViewEvent event = builder.setEventId("my-unique-id").build();
...
CxenseSdk.getInstance().trackActiveTime(event.getEventId());
```
> This event id is used internally only as key

#### Track content (instead of pages, it is actual for OTT applications)
The SDK allows you tracking content views by leveraging page view events. If your content do not have own URL (streamed content for example), but can be identified through some alphanumerical string, than you can track it's views too. Just set your content id to `PageViewEvent.Builder` instance:
```java
builder.setContentId("897983476897356");
```
> `contentId` & `location` properties are mutually exclusive. Please use either content identifier or page's address.

### Performance Events
Performance events are events that means that user have performed some action in the application like 'Add item to the cart'. Performance events are aggregated by Cxense DMP and all collected events of that type are available for analysis in DMP's web interface.

Use `PerformanceEvent` class to track performance events in your application. Instances of the class can be easily created using `PerformanceEvent.Builder`:
```java
PerformanceEvent.Builder builder = new PerformanceEvent.Builder(Collections.singletonList(new UserIdentity("xyz", "abcdefg12345")), "[put your SiteId here]", "xyz-sample", "click");
```
`PerformanceEvent.Builder` class provide set of methods through which you can easily configure data that will be applied on performance event. Here is example of event's configuration (pay attention that method's invocations can be chained):
```java
// Custom parameters on performance events have different structure than custom parameters of page view events.
// They have more complex structure and can be configured through 'DMPCustomParameter' class usage.
builder.addCustomParameters(new CustomParameter("val", "0.34"),
           new CustomParameter("campaign", "ad"))
        // Identifier of segments are also can be provided to event's data.
        .addSegments("123", "456");
```
Also, reported page view events can be attached to performance events. That is how that can be implemented:
```java
PageViewEvent event = ... // get instance of PageViewEvent that must be attached to PerformanceEvent
CxenseSdk.getInstance().pushEvents(event);
 
// while constructing new instance of PerformanceEvent using PerformanceEvent.Builder just use following method to associate pv event with perf event: 
...
.setPrnd(event.getRnd())
... 
```
To schedule event's sending on closest dispatch loop's iteration, use following method for performance events too:
```java
CxenseSdk.getInstance().pushEvents(...);
```
> Cxense SDK automatically tracks meta information (like name and version) of application in which it is used by default. This will help you in understanding which version of the application had generated events and will help you in distinguishing traffic came from application from traffic came from web (if both platforms are supported in your product). Automatic tracking uses custom parameters to deliver meta information and can be disabled by setting to false following property of your `CxenseConfiguration` object: `CxenseSdk.getInstance().getConfiguration().setAutoMetaInfoTrackingEnabled(false);`

### Conversion Events
Conversion events are CCE events described [here](https://wiki.cxense.com/pages/viewpage.action?pageId=35885415) (*contact Cxense for access*)

Use `ConversionEvent` class to track performance events in your application. Instances of the class can be easily created using `ConversionEvent.Builder`:
```java
ConversionEvent.Builder builder = new ConversionEvent.Builder(singletonList(new UserIdentity("xyz", "abcdefg12345")), "[put your SiteId here]", "productID", ConversionEvent.FUNNEL_TYPE_CONVERT_PRODUCT);
```
`ConversionEvent.Builder` class provide set of methods through which you can easily configure data that will be applied on conversion event. Here is example of event's configuration (pay attention that method's invocations can be chained):
```java
// Custom parameters on performance events have different structure than custom parameters of page view events.
// They have more complex structure and can be configured through 'DMPCustomParameter' class usage.
builder.setPrice(12.25).setRenewalFrequency("1wC")
```
To schedule event's sending on closest dispatch loop's iteration, use following method for conversion events too:
```java
CxenseSdk.getInstance().pushEvents(...);
```

## Advanced scenarios
#### Using custom User Id
```java
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
 
// Get default user id. Current value is Advertising ID
String defaultUserId = cxenseSdk.getDefaultUserId();
 
// Get current user id
String currentUserId = cxenseSdk.getUserId()
 
// Set custom user id
cxenseSdk.setUserId(customUserId);
```
#### Working with user consent (GDPR) 
If user consent is required in your application you should add ConsentOption.CONSENT_REQUIRED to enable checking consents before event processing. Add additional consent flags after requesting it from user. 
```java
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
Set<ConsentOption> currentOptions = cxenseSdk.getConsentOptions();
currentOptions.add(ConsentOption.CONSENT_REQUIRED, ConsentOption.SEGMENT_ALLOWED);
```
> Pay attention that if given consent options may affect not only data processing on backend, but also affect SDK's functionality.

Possible consent flags (only if ConsentOption.CONSENT_REQUIRED option was added):
| Option | Description |
| --- | --- |
| ConsentOption.PV_ALLOWED | Allows page view tracking, DMP event tracking and browsing habit collection to understand a user’s interests and profile. |
| ConsentOption.RECS_ALLOWED | Allows personalisation of content recommendations and suggested content based on user interests and browsing habits |
| ConsentOption.SEGMENT_ALLOWED | Allows audience segmentation, processing of browsing habits and first party data to include users in specific audience segments. |
| ConsentOption.AD_ALLOWED | Allows targeting advertising based on browsing habits and audience segmentation. |
#### Retrieve segments for a user
```java
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
List<UserIdentity> identities = new ArrayList<>();
identities.add(new UserIdentity(type, id));
identities.add(new UserIdentity(type2, id2));
cxenseSdk.getUserSegmentIds(identities, Arrays.asList(siteGroupId, siteGroupId2), new LoadCallback<List<String>>() {
    @Override
    public void onSuccess(List<String> data) {
        // do something with data
    }
    @Override
    public void onError(Throwable t) {
        // do something with error
    }
});
```
#### Retrieve user profile
```java
String id = "john.doe@example.com";
String type = "xyz";
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
cxenseSdk.getUser(new UserIdentity(id, type), new LoadCallback<User>() {
    @Override
    public void onSuccess(User data) {
        // do something with data
    }
    @Override
    public void onError(Throwable t) {
    // do something with error
    }
});
```
#### Retrieve content recommendations
Configure context for widget
```java
// there is the only required parameter for a context: URL
WidgetContext widgetContext = new WidgetContext.Builder("YOUR_URL")
                // override other optional properties
                .setReferrer(...)
                .setKeywords(...)
                .setNeighbors(...)
                .setParameters(...)
                .build();
```   
Fetch recommendations
```java
CxenseSdk.getInstance().loadWidgetRecommendations(widgetId, widgetContext, new LoadCallback<List<WidgetItem>>() {
    @Override
    public void onSuccess(List<WidgetItem> data) {
        // work with loaded items
    }
    @Override
    public void onError(Throwable throwable) {
        // process error
    }
});
```
Track item click
```java
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
// for item
cxenseSdk.trackClick(item, new LoadCallback() {
    @Override
    public void onSuccess(Object data) {
        // success response from server
    }
 
    @Override
    public void onError(Throwable throwable) {
        // process error
    }
});
// for custom url
cxenseSdk.trackClick(url, new LoadCallback() {
    @Override
    public void onSuccess(Object data) {
        // success response from server
    }
    @Override
    public void onError(Throwable throwable) {
        // process error
    }
});
```
#### Some debug methods
Get event sending queue status
```java
CxenseSdk.getInstance().getQueueStatus();
```
Set callback for each dispatch
```java
CxenseSdk.getInstance().setDispatchEventsCallback(statuses -> {
        for (EventStatus eventStatus : statuses) {
            if (eventStatus.exception != null) {
                Log.e(TAG, String.format(Locale.getDefault(), "Error at sending event with id '%s'", eventStatus.eventId), eventStatus.exception);
            }
        }
);
```
Force flush event queue
```java
CxenseSdk.getInstance().flushEventQueue();
```
#### Working with external data
```java
String id = "john.doe@example.com";
String type = "xyz";
CxenseSdk cxenseSdk = CxenseSdk.getInstance();
 
// read external data for all users with type
cxenseSdk.getUserExternalData(type, new LoadCallback<List<UserExternalData>>() {
    @Override
    public void onSuccess(List<UserExternalData> data) {
        // do something with data
    }
 
    @Override
    public void onError(Throwable t) {
        // do something with error
    }
});
 
// update external data for user
UserExternalData userExternalData = new UserExternalData.Builder(new UserIdentity(type, id))
        .addExternalItem("group1", "item1")
        .addExternalItem("group2", "item2")
        .addExternalItem("group3", "item3")
        .build();
cxenseSdk.setUserExternalData(userExternalData, new LoadCallback<Void>() {
    @Override
    public void onSuccess(Void data) {
        // do something at success (data not available)
    }
 
    @Override
    public void onError(Throwable t) {
        // do something with error
    }
});
 
// delete external data for user
cxenseSdk.deleteUserExternalData(new UserIdentity(type, id), new LoadCallback<Void>() {
    @Override
    public void onSuccess(Void data) {
        // do something at success (data not available)
    }
 
    @Override
    public void onError(Throwable t) {
        // do something with error
    }
});
```
> Updating external data will overwrite any existing data associated with the user. Hence, if the intention is to add a new key-value to the user without erasing the existing information, the client should first read the currently stored data, add the new data to the already stored data, and upload the new profile consisting of both the previously stored information,and the new information.

#### Using persisted queries
Cxense SDK provides also support of Persisted Queries. It contains two methods in its public API that are work with the queries:
```java
/**
 * Executes persisted query to Cxense API endpoint. You can find some popular endpoints in {@link CxenseConstants}
 *
 * @param url               API endpoint
 * @param persistentQueryId query id
 * @param callback          callback for response data
 * @param <T>               response type
 */
public <T> void executePersistedQuery(String url, String persistentQueryId, LoadCallback<T> callback);
 
/**
 * Executes persisted query to Cxense API endpoint. You can find some popular endpoints in {@link CxenseConstants}
 *
 * @param url               API endpoint
 * @param persistentQueryId query id
 * @param data              data for sending as request body
 * @param callback          callback for response data
 * @param <T>               response type
 */
public <T> void executePersistedQuery(String url, String persistentQueryId, Object data, LoadCallback<T> callback);
```
It is generic API and you can use it with any Cxense API, that supports persisted queries,if you provide correct response object definition. SDK includes urls and definitions for popular API endpoints.

Sample usage:
We have persisted query for `/profile/user/segment` with request body `{"identities":[{"id":"0","type":"cx"}],"siteGroupIds":["1234567890123456789"]}` and mutable fields: `identities`
```java
UserSegmentRequest data = new UserSegmentRequest(Collections.singletonList(new UserIdentity(type, id)), null);
CxenseSdk.getInstance().executePersistedQuery(CxenseConstants.ENDPOINT_USER_SEGMENTS, "some_persistent_id", data, new LoadCallback<SegmentsResponse>() {
    @Override
    public void onSuccess(SegmentsResponse segmentsResponse) {
        // do something with segmentsResponse.ids
    }
 
    @Override
    public void onError(Throwable throwable) {
        // do something with error
    }
});
```
