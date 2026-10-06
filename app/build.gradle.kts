import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.gms.google-services")
    kotlin("kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.yarik.watcher"
    compileSdk = 36

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    defaultConfig {
        applicationId = "com.yarik.watcher"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "boolean",
            "ENABLE_HTTP_LOGS",
            localProperties.getProperty("ENABLE_HTTP_LOGS", "false"),
        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.fragment:fragment-ktx:1.8.8")
    implementation("androidx.fragment:fragment-compose:1.8.8")

    implementation("com.github.terrakok:cicerone:7.1")

    implementation("com.google.dagger:dagger-android:2.56.2")
    implementation("com.google.dagger:dagger-android-support:2.56.2")
    kapt("com.google.dagger:dagger-compiler:2.56.2")

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0")

    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.work:work-runtime-ktx:2.11.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation(project(":utils"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}