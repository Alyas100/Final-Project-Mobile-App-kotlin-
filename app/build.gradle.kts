plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    // firebase crashlytics
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.finalprojectmobileapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finalprojectmobileapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.fido)
    implementation(libs.googleid)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.location)
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")


    // dependencies for google identity services for authnetication user sign in with google
        // Google Identity Services (for Google Sign-In with Credential Manager)
    // Add these dependencies
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.6.0")  // google services
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0") // Google Identity

    // Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics:18.3.7")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics:21.5.0")

    // Two dependencies below is coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")   // this core version required for coroutine support.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")    // this android version enables coroutines on Android for UI-related tasks.

    // This is material components dependancy
    implementation("com.google.android.material:material:1.9.0")


    // This is gemini ai dependencies
    // The retrofit here is used to call the Gemini AI API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // This is Gson that heps parse JSON response from the ai server
    implementation("com.google.code.gson:gson:2.8.9")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")










}