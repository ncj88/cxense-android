import org.gradle.api.JavaVersion

object Config {
    // Android config
    const val androidBuildTools = "29.0.2"
    const val androidMinSdk = 15
    const val androidTargetSdk = 29
    const val androidCompileSdk = 29
    const val authority = "CxSdkInitProvider"
    val compileSourceVersion = JavaVersion.VERSION_1_8
    val compileTargetVersion = JavaVersion.VERSION_1_8
}

object Versions {
    const val kotlin = "1.3.60"
    //Plugins
    const val buildScanPlugin = "2.4.2"
    const val versionsPlugin = "0.25.0"
    const val releasePlugin = "1.10.2"
    const val androidToolsPlugin = "4.0.0-alpha04"
    const val androidMavenPlugin = "2.1"
    const val spotbugsPlugin = "2.0.1"
    const val ktlint = "9.1.1"
    const val dokka = "0.10.0"
    // Android libraries
    const val compatLibrary = "1.1.0"
    const val annotationsLibrary = "1.1.0"
    const val materialLibrary = "1.0.0"

    // Third party Libraries
    const val googlePlayServices = "17.0.0"
    const val retrofit = "2.6.2"
    const val okhttp = "3.12.6"
    const val timber = "4.7.1"

    // Test Libraries
    const val junit = "4.12"
    const val mockitoKotlin = "2.2.0"
    const val mockitoCore = "3.0.0"
}

object Plugins {
    const val kotlin = "gradle-plugin"
    const val versions = "com.github.ben-manes.versions"
    const val release = "pl.allegro.tech.build.axion-release"
    const val androidTools = "com.android.tools.build:gradle:${Versions.androidToolsPlugin}"
    const val androidMavenPlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.androidMavenPlugin}"
    const val androidApp = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExt = "kotlin-android-extensions"
    const val androidMaven = "com.github.dcendents.android-maven"
    const val spotbugs = "com.github.spotbugs"
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val dokka = "org.jetbrains.dokka"
}

object Libs {
    const val kotlinStdlib = "stdlib-jdk8"
    const val annotations = "androidx.annotation:annotation:${Versions.annotationsLibrary}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.compatLibrary}"
    const val material = "com.google.android.material:material:${Versions.materialLibrary}"

    const val googleAds = "com.google.android.gms:play-services-ads-identifier:${Versions.googlePlayServices}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"


    const val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoCore = "org.mockito:mockito-core:${Versions.mockitoCore}"
    const val junit = "junit:junit:${Versions.junit}"
    const val okhttpMockServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
}
