plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.secrets)
}

android {
    namespace = "com.example.cxensesdk"
    defaultConfig {
        minSdk = 21
        compileSdk = 34
        targetSdk = 34
        applicationId = "com.example.cxensesdk"
        versionCode = 1
        versionName = "1.0"

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

secrets {
    propertiesFileName = "cxense.properties"
    defaultPropertiesFileName = "cxense.defaults.properties"
}

dependencies {
    implementation(project(":sdk"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerView)
    implementation(libs.viewBindingProperty)
}
