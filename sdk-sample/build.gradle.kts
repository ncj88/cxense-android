import com.cxense.cxensesdk.dependencies.Libs

plugins {
    id("com.android.application")
    id("common-android-config")
}

android {
    defaultConfig {
        applicationId = "com.example.cxensesdk"
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SITEGROUP_ID", """"9222291112880224990"""")
        buildConfigField("String", "SITE_ID", """"1131746995643019840"""")
        buildConfigField("String", "USERNAME", """"PUT_USERNAME_HERE"""")
        buildConfigField("String", "API_KEY", """"PUT_API_KEY_HERE"""")
        buildConfigField("String", "PERSISTED_ID", """"PUT_PERSISTED_ID_HERE"""")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":sdk"))
    implementation(Libs.appcompat)
    implementation(Libs.material)
    implementation(Libs.viewBindingProperty)
}
