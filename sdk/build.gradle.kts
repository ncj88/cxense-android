plugins {
    id(Plugins.androidLibrary)
    id(Plugins.androidMaven)
    id(Plugins.spotbugs)
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
        buildConfigField("String", "CX_USER", """"${project.property("CX_USER") ?: ""}"""")
        buildConfigField("String", "CX_KEY", """"${project.property("CX_KEY") ?: ""}"""")
        buildConfigField("String", "CX_SITE_ID", """"${project.property("CX_SITE_ID") ?: ""}"""")

        manifestPlaceholders = mapOf("authority" to Config.authority)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("cxensesdk.pro")
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
    val javadoc by creating(Javadoc::class) {
        source = android.sourceSets["main"].java.sourceFiles
        classpath += files(android.bootClasspath)
        classpath += configurations["compile"]
        isFailOnError = false
        exclude("**/BuildConfig.java", "**/R.java")
        (options as StandardJavadocDocletOptions).apply {
            encoding = "UTF-8"
            links("http://docs.oracle.com/javase/7/docs/api/", "http://developer.android.com/reference/")
            linksOffline("http://d.android.com/reference", "${android.sdkDirectory}/docs/reference")
            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    val javadocJar by creating(Jar::class) {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.destinationDir)
    }

    artifacts.add("archives", javadocJar)

    val updates by rootProject.tasks.named("dependencyUpdates")
    named("build") {
        dependsOn(updates)
    }
}

version = rootProject.version

val checkStyleConfigDir = "${project.rootDir}/config/"
checkstyle {
    configFile = file("$checkStyleConfigDir/checkstyle-rules.xml")
    isIgnoreFailures = true
    isShowViolations = true
}


spotbugs {
    excludeBugsFilter = file("$checkStyleConfigDir/findbugs-exclude-filter.xml")
    effort = "max"
    reportLevel = "high"
    isIgnoreFailures = true
}

pmd {
    ruleSetFiles = files("$checkStyleConfigDir/pmd-ruleset.xml")
    isIgnoreFailures = true
}

dependencies {
    api(Libs.annotations)
    implementation(Libs.googleAds)
    api(Libs.retrofit)
    api(Libs.retrofitConverter)
    api(Libs.okhttpLogging)

    testImplementation(Libs.powermockJunit)
    testImplementation(Libs.powermockMockito)
    testImplementation(Libs.junit)
    testImplementation(Libs.okhttpMockServer)
    testImplementation(Libs.hamcrest)

    androidTestImplementation(Libs.testRunner)
    androidTestImplementation(Libs.testJunitExt)
    androidTestImplementation(Libs.testCore)
}
