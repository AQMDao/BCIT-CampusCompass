/*
    The top-level build.gradle.kts file (for the Kotlin DSL) or build.gradle file
    (for the Groovy DSL) is located in the root project directory. It typically defines the common
    versions of plugins used by modules in your project.
*/

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

/*
    Buildscript is used to configure the repositories and dependencies for Gradle.
    This dependencies block inside is used to configure dependencies that the Gradle
    needs to build during the project.
*/
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}