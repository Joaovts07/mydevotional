plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.mydevotional"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mydevotional"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.1"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //Firebase
    implementation(libs.firebase.auth.ktx)

    //compose calendar
    implementation(libs.material)

    //KTOR
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.kotlinx.coroutines.android)

    //icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    //navigation
    implementation(libs.androidx.navigation.compose)

    //google firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.firestore.ktx)

    //calendar
    implementation(libs.accompanist.pager)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.core.ktx)
    kapt(libs.hilt.android.compiler )
    implementation(libs.androidx.hilt.navigation.compose)

    //data store
    implementation(libs.androidx.datastore.preferences)

    //ML Kit
    implementation(libs.text.recognition)

    //ROOM
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler) 




    //login
    implementation(project(":login"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}