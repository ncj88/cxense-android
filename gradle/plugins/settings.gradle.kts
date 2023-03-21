dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
            // We add plugins as dependencies for our configuration plugins. Added here for not exposing them to project
            library("versionUpdater", "com.github.ben-manes", "gradle-versions-plugin").versionRef("versionUpdater")
        }
    }
}
