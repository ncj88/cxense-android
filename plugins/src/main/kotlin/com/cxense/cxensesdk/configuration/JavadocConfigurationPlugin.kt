package com.cxense.cxensesdk.configuration

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

class JavadocConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        configureJavadoc()
    }

    private fun Project.configureJavadoc() {
        apply<DokkaPlugin>()
        tasks.apply {
            named<DokkaTask>("dokkaHtml") {
                outputDirectory.set(file("$buildDir/doc"))
            }
            val dokkaJavadoc = named<DokkaTask>("dokkaJavadoc") {
                outputDirectory.set(file("$buildDir/javadoc"))
            }
            register<Jar>("javadocJar") {
                dependsOn(dokkaJavadoc)
                archiveClassifier.set("javadoc")
                from(dokkaJavadoc.get().outputDirectory.get())
            }
        }
    }
}
