import com.cxense.cxensesdk.dependencies.Libs

plugins {
    id("com.android.library")
    id("common-android-config")
    id("javadoc-config")
    kotlin("kapt")
    `maven-publish`
}

android {
    defaultConfig {
        val authority = "CxSdkInitProvider"
        buildConfigField("String", "SDK_VERSION", """"${rootProject.version}"""")
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

    packagingOptions {
        exclude("META-INF/LICENSE")
    }

    lintOptions {
        isAbortOnError = false
    }
}

version = rootProject.version

dependencies {
    api(Libs.annotations)
    implementation(Libs.startup)
    implementation(Libs.googleAds)
    api(Libs.retrofit)
    api(Libs.retrofitConverter)
    implementation(Libs.moshi)
    kapt(Libs.moshiCodegen)
    api(Libs.okhttpLogging)
    api(Libs.timber)

    testImplementation(Libs.kotlinJunit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.junit)
    testImplementation(Libs.okhttpMockServer)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = group.toString()
                artifactId = "cxense-android"
                version = project.version.toString()

                artifact(tasks.named("javadocJar"))

                pom {
                    inceptionYear.set("2017")
                    developers {
                        developer {
                            id.set("cXense")
                            name.set("cXense")
                        }
                    }
                }
            }
        }
    }
}
