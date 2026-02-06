plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.yolo.vozilo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yolo.vozilo"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- ADDED SIGNING CONFIG ---
    signingConfigs {
        create("release") {
            // Checks for local file in project root, otherwise looks for GitHub Runner file
            val localKey = rootProject.file("keystore/android-key")
            val githubKey = rootProject.file("release.keystore")

            storeFile = if (localKey.exists()) localKey else githubKey

            // Injected via environment variables in GitHub Actions or your local gradle.properties
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // --- ATTACH THE SIGNING CONFIG ---
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures { compose = true }

    @Suppress("UnstableApiUsage")
    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Networking & Images
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // AI & Vision
    implementation("com.google.mlkit:object-detection:17.0.0")
    implementation("com.google.mlkit:object-detection-custom:17.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}