import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.properties.TagProperties
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id(Plugins.versions) version Versions.versionsPlugin
    id(Plugins.release) version Versions.releasePlugin
    id(Plugins.spotbugs) version Versions.spotbugsPlugin
}

buildscript {
    repositories {
        google()
        jcenter()

    }

    dependencies {
        classpath(Plugins.androidTools)
        classpath(Plugins.androidMavenPlugin)
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
        initialVersion = KotlinClosure2({ _: TagProperties, _: ScmPosition -> "1.0.0"}, this, this)
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

tasks {
    register("clean", Delete::class) {
        delete(buildDir)
    }
    named<DependencyUpdatesTask>("dependencyUpdates") {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                    if (candidate.group == "com.squareup.okhttp3") {
                        val version = candidate.version.split(".")
                        if (version.component1().toInt() > 3 || version.component2().toInt() > 12)
                            reject("We use okhttp 3.12.*, because okhttp 3.13+ requires API 21")
                    }
                }
            }
        }
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }
}

