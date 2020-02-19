import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.properties.TagProperties
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id(Plugins.versions) version Versions.versionsPlugin
    id(Plugins.release) version Versions.releasePlugin
    id(Plugins.spotbugs) version Versions.spotbugsPlugin
    id(Plugins.ktlint) version Versions.ktlint
    id(Plugins.androidMaven) version Versions.androidMavenPlugin
    id(Plugins.dokka) version Versions.dokka
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Plugins.androidTools)
        classpath(kotlin(Plugins.kotlin, Versions.kotlin))
    }
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

scmVersion {
    tag(closureOf<TagNameSerializationConfig> {
        prefix = ""
        initialVersion = KotlinClosure2({ _: TagProperties, _: ScmPosition -> "1.0.0" }, this, this)
    })
    rootProject.version = version
}

allprojects {
    repositories {
        google()
        jcenter()
        // temporary fix
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

// We use okhttp 3.12.* and retrofit 2.6.*, because okhttp 3.13+ requires API 21 and retrofit 2.7+ requires API 21
val maxSupportedSquareLib = mapOf(
    "com.squareup.okhttp3" to arrayOf(3, 12),
    "com.squareup.retrofit2" to arrayOf(2, 6)
)

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return !isStable
}

fun isNotSupportedSquareLib(group: String, version: String): Boolean =
    maxSupportedSquareLib.any { (key, value) ->
        group == key && version.split(".")
            .map { it.toInt() }
            .zip(value)
            .any { it.first > it.second }
    }

tasks {
    named<DependencyUpdatesTask>("dependencyUpdates") {
        rejectVersionIf {
            isNotSupportedSquareLib(candidate.group, candidate.version)
                    || isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }
}
