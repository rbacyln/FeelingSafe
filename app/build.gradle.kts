plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.feelingsafe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.feelingsafe"
        minSdk = 31
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// DÜZELTME: Değişken tanımı "def" yerine "val" ile yapılıyor.
val room_version = "2.6.1"

dependencies {
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.code.gson:gson:2.10.1") // Gson kütüphanesi
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // DÜZELTME: Room Kütüphanesi Bağımlılıkları Kotlin Script formatında
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
}