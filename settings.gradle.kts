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

includeBuild("plugins")

include(":sdk", ":sdk-sample")
