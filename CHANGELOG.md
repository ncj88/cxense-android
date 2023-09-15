# Piano DMP & Content SDK for Android

## v2.5.0
* Android 14 compatibility
* Fix `sdkv=unspecified` issue
* Fix incorrect symbols in User-Agent from some devices
* Add support for `typed` segments
* Add support for `typed` external items for user data
* Add Kotlin coroutine suspend support for API
* Updated dependencies:
    - Kotlin [1.7.22 -> 1.8.21]
      https://kotlinlang.org/
    - com.squareup.moshi:moshi [1.14.0 -> 1.15.0]
      https://github.com/square/moshi/
    - com.squareup.okhttp3:okhttp [4.10.0 -> 4.11.0]
      https://square.github.io/okhttp/
    - androidx.annotation:annotation [1.5.0 -> 1.6.0]
      https://developer.android.com/jetpack/androidx/releases/annotation#1.6.0

## v2.4.0
* Increased minSdkVersion from 19 to 21
* Migrate from Moshi Codegen to Moshi-IR
* Migrate PV event to new host
* Updated dependencies:
    - Kotlin [1.6.21 -> 1.7.22]
      https://kotlinlang.org/
    - androidx.startup:startup-runtime [1.1.0 -> 1.1.1]
      https://developer.android.com/jetpack/androidx/releases/startup#1.1.1
    - androidx.annotation:annotation [1.4.0 -> 1.5.0]
      https://developer.android.com/jetpack/androidx/releases/annotation#annotation-1.5.0
    - com.squareup.moshi:moshi [1.13.0 -> 1.14.0]
      https://github.com/square/moshi/
    - com.squareup.okhttp3:okhttp [3.12.13 -> 4.10.0]
      https://square.github.io/okhttp/
    - com.squareup.retrofit2:retrofit [2.6.4 -> 2.9.0]
      https://github.com/square/retrofit

## v2.3.1
* Fix C1X compatibility

## v2.3.0
* Renamed to Piano DMP & Content SDK
* Changed package, group and artifact id
* Android 13 compatibility
* The first Maven Central release
* Updated dependencies:
    - Kotlin [1.5.30 -> 1.6.21]
      https://kotlinlang.org/
    - androidx.appcompat:appcompat [1.3.1 -> 1.4.2]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.4.2
    - androidx.startup:startup-runtime [1.1.0 -> 1.1.1]
      https://developer.android.com/jetpack/androidx/releases/startup#1.1.1
    - androidx.annotation:annotation [1.2.0 -> 1.4.0]
      https://developer.android.com/jetpack/androidx/releases/annotation#annotation-1.4.0
    - com.squareup.moshi:moshi [1.12.0 -> 1.13.0]
      https://github.com/square/moshi/
    - com.squareup.okhttp3:okhttp [3.12.6 -> 3.12.13]
      https://github.com/square/okhttp/

## v2.2.0
* Android 12 compatibility
* Piano C1X support 
* Updated dependencies:
    - Kotlin [1.5.10 -> 1.5.30]
      https://kotlinlang.org/
    - androidx.appcompat:appcompat [1.3.0 -> 1.3.1]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.3.1
    - androidx.startup:startup-runtime [1.0.0 -> 1.1.0]
      https://developer.android.com/jetpack/androidx/releases/startup#1.1.0
    - com.android.tools.build:gradle [4.2.1 -> 7.0.2]
      http://tools.android.com/


## 2.1.0
* Auto-initialization has been migrated to `androidx.startup` library
* Added custom parameter `sdk_version` to all PV events
* Fixed bug with "response body is null" for some APIs
* Added consent version setting and new consents `deviceAllowed` and `geoAllowed`.
* Changed API for setting consents. Use `ConsentSettings` instead of `ConsentOption`
* Updated dependencies:
    - Kotlin [1.4.30 -> 1.5.10]
    - androidx.appcompat:appcompat [1.2.0 -> 1.3.0]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.3.0
    - com.android.tools.build:gradle [4.1.2 -> 4.2.1]
      http://tools.android.com/
    - com.squareup.moshi:moshi [1.11.0 -> 1.12.0]
      https://github.com/square/moshi

## 2.0.2
* Update target and compile SDK version from 29 to 30
* Changed GSON to Moshi as JSON parser
* Fixed rare issues with multi-process app webviews
* Fixed bug with `time` field in Performance events
* Fixed "user segment" persisted query
* Updated dependencies:
    - Kotlin [1.3.72 -> 1.4.30]
    - androidx.appcompat:appcompat [1.1.0 -> 1.2.0]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.2.0
    - com.android.tools.build:gradle [4.1.0 -> 4.2.1]
      http://tools.android.com/

## 2.0.1
* Fixed proguard rules
* Updated dependencies:
    - Kotlin [1.3.61 -> 1.3.72]
    - com.android.tools.build:gradle [4.0.0 -> 4.1.0]
      http://tools.android.com/

## 2.0.0
* SDK has been rewritten in Kotlin
* Updated dependencies:
    - com.android.tools.build:gradle [3.6.0 -> 4.0.0]
      http://tools.android.com/
