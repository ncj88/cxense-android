import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig
import pl.allegro.tech.build.axion.release.domain.properties.TagProperties
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    id("dependencies-updater")
    id("ktlint-config")
    id("pl.allegro.tech.build.axion-release") version "1.13.3"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

scmVersion {
    tag(
        closureOf<TagNameSerializationConfig> {
            prefix = ""
            initialVersion = KotlinClosure2({ _: TagProperties, _: ScmPosition -> "1.0.0" }, this, this)
        }
    )
    rootProject.version = version
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
