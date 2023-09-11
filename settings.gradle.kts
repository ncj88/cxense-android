pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.13"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

includeBuild("gradle/plugins")

include(":sdk", ":sdk-sample")
