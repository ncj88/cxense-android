pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("com.gradle.enterprise") version "3.0"
}

includeBuild("dependencies")

include(":sdk", ":sdk-sample")
