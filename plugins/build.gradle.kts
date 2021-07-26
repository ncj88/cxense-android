plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.cxense.cxensesdk.plugins"
version = "SNAPSHOT"

// Required since Gradle 4.10+.
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.ben-manes:gradle-versions-plugin:0.39.0")
    implementation("com.android.tools.build:gradle:4.2.2")
    implementation(kotlin("gradle-plugin", "1.5.21"))
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.1.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.5.0")
}

gradlePlugin {
    plugins {
        register("dependencies") {
            id = "dependencies"
            implementationClass = "com.cxense.cxensesdk.dependencies.DependenciesPlugin"
        }
        register("dependencies-updater") {
            id = "dependencies-updater"
            implementationClass = "com.cxense.cxensesdk.dependencies.DependenciesUpdaterPlugin"
        }
        register("common-android-config") {
            id = "common-android-config"
            implementationClass = "com.cxense.cxensesdk.configuration.CommonAndroidConfigurationPlugin"
        }
        register("ktlint-config") {
            id = "ktlint-config"
            implementationClass = "com.cxense.cxensesdk.ktlint.KtlintConfigPlugin"
        }
        register("javadoc-config") {
            id = "javadoc-config"
            implementationClass = "com.cxense.cxensesdk.configuration.JavadocConfigurationPlugin"
        }
    }
}
