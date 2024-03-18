// module-level build.gradle

plugins {
    id("com.android.application")

    /*
    *   Step 3: Setting up the Secrets Gradle Plugin to add our API Key 2/10
    *   Add the Secrets Gradle Plugin ID in the plugins element
    */
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

/*
*   Step 2: Setting up the Maps SDK for Android 4/5
*   Set the correct compileSdk and minSdk values
*   Requires compileSdk >= 34 and minSdk >= 19
*/
android {
    namespace = "com.example.bcit_campuscompass"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bcit_campuscompass"
        minSdk = 19
        /*
        *   Step 3: Setting up the Secrets Gradle Plugin to add our API Key  3/10
        *   Ensure targetSdk and compileSdk are set to 34
        *
        *   Step 3: Setting up the Secrets Gradle Plugin to add our API Key  4/10
        *   Save the file and sync the project with Gradle
        */
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

    /*
    *   Step 2: Setting up the Maps SDK for Android 5/5
    *   Create a buildFeatures block under the android block
    *   Add the buildConfig class, used to access defined metadata values
    */
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /*
     *   Step 2: Setting up the Maps SDK for Android 3/5
     *   Add the Google Play Services dependency for the Maps SDK for Android
     */
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}

/*
*   Step 3: Setting up the Secrets Gradle Plugin to add our API Key  10/10
*   Create the secrets property and set the propertiesFileName and defaultPropertiesFileName
*/
secrets {
    // Specify a different file name containing our secrets
    propertiesFileName = "secrets.properties"
    // Specify a different file name containing default secret values
    // This file can be checked in version control, refer to local.defaults.properties
    defaultPropertiesFileName = "local.defaults.properties"
}