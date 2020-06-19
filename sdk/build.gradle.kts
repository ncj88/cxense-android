import org.jetbrains.dokka.gradle.DokkaTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.dokka)
    id(Plugins.androidMaven)
    id(Plugins.spotbugs)
    id(Plugins.ktlint)
    checkstyle
    pmd
}

android {
    buildToolsVersion = Config.androidBuildTools
    compileSdkVersion(Config.androidCompileSdk)

    defaultConfig {
        minSdkVersion(Config.androidMinSdk)
        targetSdkVersion(Config.androidTargetSdk)
        versionName = rootProject.version.toString()

        buildConfigField("String", "SDK_NAME", """"cxense"""")
        buildConfigField("String", "SDK_ENDPOINT", """"https://api.cxense.com"""")
        buildConfigField("String", "AUTHORITY", """LIBRARY_PACKAGE_NAME + ".${Config.authority}"""")

        manifestPlaceholders = mutableMapOf<String, Any>("authority" to Config.authority)

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
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/doc"
        configuration {
            reportUndocumented = true
        }
    }

    val javadoc by creating(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
        configuration {
            reportUndocumented = true
        }
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.outputDirectory)
    }

    artifacts.add("archives", javadocJar)
}

version = rootProject.version

val checkStyleConfigDir = "${project.rootDir}/config/"
checkstyle {
    configFile = file("$checkStyleConfigDir/checkstyle-rules.xml")
    isIgnoreFailures = true
    isShowViolations = true
}

spotbugs {
    excludeFilter.set(file("$checkStyleConfigDir/findbugs-exclude-filter.xml"))
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.HIGH)
    ignoreFailures.set(true)
}

pmd {
    ruleSetFiles = files("$checkStyleConfigDir/pmd-ruleset.xml")
    isIgnoreFailures = true
}

ktlint {
    android.set(true)
}

dependencies {
    implementation(kotlin(Libs.kotlinStdlib, Versions.kotlin))
    api(Libs.annotations)
    implementation(Libs.googleAds)
    api(Libs.retrofit)
    api(Libs.retrofitConverter)
    api(Libs.okhttpLogging)
    api(Libs.timber)

    testImplementation(Libs.kotlinJunit)
    testImplementation(Libs.mockitoKotlin)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.junit)
    testImplementation(Libs.okhttpMockServer)
}
