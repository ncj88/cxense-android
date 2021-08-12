package com.cxense.cxensesdk.dependencies

private object Versions {
    const val kotlin = "1.5.21"

    // Android/Jetpack libraries
    const val compatLibrary = "1.3.1"
    const val annotationsLibrary = "1.2.0"
    const val materialLibrary = "1.4.0"
    const val startupLibrary = "1.1.0"

    // Third party Libraries
    const val googlePlayServices = "17.0.1"
    const val retrofit = "2.6.4"
    const val okhttp = "3.12.6"
    const val moshi = "1.12.0"
    const val timber = "5.0.0"
    const val viewBindingProperty = "1.4.7"

    // Test Libraries
    const val junit = "4.13.2"
    const val mockitoKotlin = "2.2.0"
    const val mockitoCore = "3.0.0"
}

object Libs {
    const val annotations = "androidx.annotation:annotation:${Versions.annotationsLibrary}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.compatLibrary}"
    const val material = "com.google.android.material:material:${Versions.materialLibrary}"
    const val startup = "androidx.startup:startup-runtime:${Versions.startupLibrary}"

    const val googleAds = "com.google.android.gms:play-services-ads-identifier:${Versions.googlePlayServices}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val viewBindingProperty = "com.github.kirich1409:viewbindingpropertydelegate:${Versions.viewBindingProperty}"


    const val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoCore}"
    const val junit = "junit:junit:${Versions.junit}"
    const val okhttpMockServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
}
