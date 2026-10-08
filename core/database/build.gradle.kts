plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.yarik.watcher.core.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // api: entity-классы попадают в сигнатуры репозитория — должны быть видны потребителям модуля
    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Annotation processor через kapt, как и в остальных модулях проекта
    kapt(libs.androidx.room.compiler)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    // DatabaseModule — обычный Dagger-модуль, паттерн как в :app
    implementation("com.google.dagger:dagger:2.56.2")
    kapt("com.google.dagger:dagger-compiler:2.56.2")
}
