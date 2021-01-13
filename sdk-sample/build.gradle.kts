plugins {
    id(Plugins.androidApp)
    id(Plugins.kotlinAndroid)
    id(Plugins.ktlint)
}

android {
    compileSdkVersion(Config.androidCompileSdk)
    buildToolsVersion(Config.androidBuildTools)

    defaultConfig {
        applicationId = "com.example.cxensesdk"
        minSdkVersion(Config.androidMinSdk)
        targetSdkVersion(Config.androidTargetSdk)
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
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = Config.compileSourceVersion
        targetCompatibility = Config.compileTargetVersion
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

ktlint {
    android.set(true)
}

dependencies {
    implementation(kotlin(Libs.kotlinStdlib, Versions.kotlin))
    implementation(project(":sdk"))
    implementation(Libs.appcompat)
    implementation(Libs.material)
    implementation(Libs.viewBindingProperty)
}
