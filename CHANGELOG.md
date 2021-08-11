# Cxense SDK for Android

## v2.2.0-SNAPSHOT
* New features will be here
* Updated dependencies:
    - Kotlin [1.5.10 -> 1.5.21]
      https://kotlinlang.org/
    - androidx.appcompat:appcompat [1.3.0 -> 1.3.1]
      https://developer.android.com/jetpack/androidx/releases/appcompat#1.3.1
    - androidx.startup:startup-runtime [1.0.0 -> 1.1.0]
      https://developer.android.com/jetpack/androidx/releases/startup#1.1.0
    - com.android.tools.build:gradle [4.2.1 -> 7.0.0]
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
