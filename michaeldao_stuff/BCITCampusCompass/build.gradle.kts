// top-level build.gradle

plugins {
    id("com.android.application") version "8.2.2" apply false
}

/*
*   Step 3: Setting up the Secrets Gradle Plugin to add our API Key  1/10
*   Add the Secrets Gradle Plugin dependency under the the buildscript block
*/
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}