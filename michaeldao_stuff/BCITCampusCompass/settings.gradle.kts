// top-level settings.gradle

/*
*   Step 2: Setting up Maps SDK for Android 1/5
*   Create a pluginManagement block placed before all other statements
*   Include the Gradle plugin portal, Google Maven, and Maven Central repositories
*/
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

/*
*   Step 2: Setting up Maps SDK for Android 2/5
*   Create a dependencyResolutionManagement block
*   Include the Google Maven, and Maven Central repositories
*/
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BCIT-CampusCompass"
include(":app")
