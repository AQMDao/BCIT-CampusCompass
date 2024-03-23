/*
    The settings.gradle.kts file (for the Kotlin DSL) or settings.gradle file (for the Groovy DSL)
    is located in the root project directory. This settings file defines project-level repository
    settings and informs Gradle which modules it should include when building your app.
*/

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

rootProject.name = "BCITCampusCompass"
include(":app")
