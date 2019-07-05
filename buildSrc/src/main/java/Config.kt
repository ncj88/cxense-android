import org.gradle.api.JavaVersion

object Config {
    // Android config
    const val androidBuildTools = "28.0.3"
    const val androidMinSdk = 15
    const val androidTargetSdk = 29
    const val androidCompileSdk = 29
    const val authority = "CxSdkInitProvider"
    val compileSourceVersion = JavaVersion.VERSION_1_8
    val compileTargetVersion = JavaVersion.VERSION_1_8
}

object Versions {
    const val kotlin = "1.3.41"
    //Plugins
    const val buildScanPlugin = "2.3"
    const val versionsPlugin = "0.21.0"
    const val releasePlugin = "1.10.1"
    const val androidToolsPlugin = "3.6.0-alpha04"
    const val androidMavenPlugin = "2.1"
    const val spotbugsPlugin = "2.0.0"
    // Android libraries
    const val compatLibrary = "1.0.2"
    const val annotationsLibrary = "1.1.0"
    const val materialLibrary = "1.0.0"

    // Third party Libraries
    const val googlePlayServices = "17.0.0"
    const val retrofit = "2.6.0"
    const val okhttp = "3.12.3"

    // Test Libraries
    const val junit = "4.12"
    const val testRunner = "1.1.1"
    const val powermock = "2.0.2"
    const val hamcrest = "2.1"
}

object Plugins {
    const val kotlin = "gradle-plugin"
    const val buildScan = "com.gradle.build-scan"
    const val versions = "com.github.ben-manes.versions"
    const val release = "pl.allegro.tech.build.axion-release"
    const val androidTools = "com.android.tools.build:gradle:${Versions.androidToolsPlugin}"
    const val androidMavenPlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.androidMavenPlugin}"
    const val androidApp = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroidApp = "kotlin-android"
    const val kotlinAndroidExtApp = "kotlin-android-extensions"
    const val androidMaven = "com.github.dcendents.android-maven"
    const val spotbugs = "com.github.spotbugs"
}

object Libs {
    const val kotlinStdlib = "stdlib-jdk7"
    const val annotations = "androidx.annotation:annotation:${Versions.annotationsLibrary}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.compatLibrary}"
    const val material = "com.google.android.material:material:${Versions.materialLibrary}"

    const val googleAds = "com.google.android.gms:play-services-ads-identifier:${Versions.googlePlayServices}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverter = "com.squareup.retrofit2:converter-jackson:${Versions.retrofit}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"


    const val junit = "junit:junit:${Versions.junit}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val testJunitExt = "androidx.test.ext:junit:${Versions.testRunner}"
    const val testCore = "androidx.test:core:${Versions.testRunner}"
    const val powermockJunit = "org.powermock:powermock-module-junit4:${Versions.powermock}"
    const val powermockMockito = "org.powermock:powermock-api-mockito2:${Versions.powermock}"
    const val okhttpMockServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"
    const val hamcrest = "org.hamcrest:hamcrest:${Versions.hamcrest}"
}