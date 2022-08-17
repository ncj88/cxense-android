plugins {
    id("dependencies-updater")
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.mavenRelease) apply false
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}
