plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenRelease)
}

android {
    defaultConfig {
        minSdk = 19
        compileSdk = 33
        targetSdk = 33
        val authority = "CxSdkInitProvider"
        buildConfigField("String", "SDK_VERSION", """"${project.version}"""")
        buildConfigField("String", "SDK_NAME", """"cxense"""")
        buildConfigField("String", "SDK_ENDPOINT", """"https://api.cxense.com"""")
        buildConfigField("String", "AUTHORITY", """LIBRARY_PACKAGE_NAME + ".$authority"""")

        manifestPlaceholders += "authority" to authority

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("cxensesdk.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    explicitApi()
}

ktlint {
    version.set("0.45.2")
    android.set(true)
}

dependencies {
    api(libs.annotations)
    implementation(libs.startup)
    implementation(libs.googleAdsId)
    api(libs.retrofit)
    api(libs.retrofitConverter)
    implementation(libs.moshi)
    kapt(libs.moshiCodegen)
    api(libs.okhttpLogging)
    api(libs.timber)

    testImplementation(libs.kotlinJunit)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.junit)
    testImplementation(libs.okhttpMockServer)
}
