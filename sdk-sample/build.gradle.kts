plugins {
    id(Plugins.androidApp)
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

        buildConfigField("String", "SITE_ID", """"PUT_SITE_ID_HERE"""")
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


}

dependencies {
    implementation(project(":sdk"))
    implementation(Libs.appcompat)
    implementation(Libs.material)
}

//dependencies {
//    implementation project(':sdk')
//    implementation 'com.android.support:appcompat-v7:27.1.1'
//    implementation 'com.android.support:recyclerview-v7:27.1.1'
//    implementation 'com.android.support:design:27.1.1'
//    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
//    implementation 'com.android.support:support-vector-drawable:27.1.1'
//}
