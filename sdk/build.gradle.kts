import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.dokka)
    id(Plugins.spotbugs)
    id(Plugins.ktlint)
    kotlin("kapt")
    `maven-publish`
}

android {
    buildToolsVersion = Config.androidBuildTools
    compileSdkVersion(Config.androidCompileSdk)

    defaultConfig {
        minSdkVersion(Config.androidMinSdk)
        targetSdkVersion(Config.androidTargetSdk)

        buildConfigField("String", "SDK_VERSION", """"${rootProject.version}"""")
        buildConfigField("String", "SDK_NAME", """"cxense"""")
        buildConfigField("String", "SDK_ENDPOINT", """"https://api.cxense.com"""")
        buildConfigField("String", "AUTHORITY", """LIBRARY_PACKAGE_NAME + ".${Config.authority}"""")

        manifestPlaceholders += "authority" to Config.authority

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("cxensesdk.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/LICENSE")
    }

    compileOptions {
        sourceCompatibility = Config.compileSourceVersion
        targetCompatibility = Config.compileTargetVersion
    }

    lintOptions {
        isAbortOnError = false
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin {
    explicitApi()
}

tasks {
    dokkaHtml.configure {
        outputDirectory.set(file("$buildDir/doc"))
    }
    dokkaJavadoc.configure {
        outputDirectory.set(file("$buildDir/javadoc"))
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory.get())
    }
}

version = rootProject.version

val checkStyleConfigDir = "${project.rootDir}/config/"

spotbugs {
    excludeFilter.set(file("$checkStyleConfigDir/findbugs-exclude-filter.xml"))
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.HIGH)
    ignoreFailures.set(true)
}

ktlint {
    android.set(true)
}

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

                artifact(tasks["javadocJar"])

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
