plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    // This defines your package for code generation.
    // It must match your MainActivity package.
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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures { compose = true }

    // CRITICAL: Prevents TFLite models from being corrupted during the build process
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

    // AI & Vision (FIXED VERSION)
    // Using task-vision resolves the "Namespace used in multiple modules" error
    //implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    //implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    //implementation("com.google.mlkit:text-recognition:16.0.1")

    implementation("com.google.mlkit:object-detection:17.0.0")
    implementation("com.google.mlkit:object-detection-custom:17.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}