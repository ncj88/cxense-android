plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "io.piano.android.plugins"
version = "SNAPSHOT"

// Required since Gradle 4.10+.
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.versionUpdater)
}

gradlePlugin {
    plugins {
        register("dependencies-updater") {
            id = "dependencies-updater"
            implementationClass = "io.piano.android.dependencies.DependenciesUpdaterPlugin"
        }
    }
}
