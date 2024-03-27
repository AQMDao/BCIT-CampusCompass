/*
    The module-level build.gradle.kts (for the Kotlin DSL) or build.gradle file (for the Groovy DSL)
     is located in each project / module / directory. It lets you configure build settings for the
     specific module it is located in.
*/

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    /*
        The Secrets Gradle Plugin for Android reads secrets, including the API key, from a
        properties file not checked into a version control system. The plugin then exposes those
        properties as variables in the Gradle-generated BuildConfig class and in the Android
        manifest file.
    */
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "org.bcit.campuscompass"
    // Maps SDK For Android: Ensure compileSdk = 34 or higher
    compileSdk = 34

    defaultConfig {
        applicationId = "org.bcit.campuscompass"
        minSdk = 28
        // Maps SDK For Android: Ensure compileSdk = 34 or higher
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    /*
        A list of build features that can be disabled or enabled in an Android project. This list
        applies to all plugin types.
    */
    buildFeatures {
        /*
            Each build configuration can define its own set of code and resources while reusing
            the parts common to all versions of your app. The Android Gradle plugin works with the
            build toolkit to provide processes and configurable settings that are specific to
            building and testing Android apps.
        */
        buildConfig = true
        viewBinding = true;
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    // Location Services
    implementation("com.google.android.gms:play-services-location:21.2.0")
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}