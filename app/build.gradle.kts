plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.depotakipai"

    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.depotakipai"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // =========================================================
    // COMPOSE
    // =========================================================

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)

    // =========================================================
    // LIFECYCLE / VIEWMODEL
    // =========================================================

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // =========================================================
    // COIL - ÜRÜN FOTOĞRAFLARI
    // =========================================================

    implementation(libs.coil.compose)

    // =========================================================
    // ROOM - VERİTABANI
    // =========================================================

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // =========================================================
    // CAMERAX
    // =========================================================

    implementation("androidx.camera:camera-core:1.6.2")
    implementation("androidx.camera:camera-camera2:1.6.2")
    implementation("androidx.camera:camera-lifecycle:1.6.2")
    implementation("androidx.camera:camera-view:1.6.2")

    // =========================================================
    // ML KIT - BARKOD
    //
    // Model uygulamanın içine paketlenir.
    // İlk kullanımda ayrıca model indirme beklenmez.
    // =========================================================

    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // =========================================================
    // ML KIT - METİN / OCR
    //
    // Etiket üzerindeki:
    // SNZ-2926
    // BEDEN: L
    // RENK: SİYAH
    // gibi bilgileri okumak için.
    // =========================================================

    implementation("com.google.mlkit:text-recognition:16.0.1")

    // =========================================================
    // TEST
    // =========================================================

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}