import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    // Firebase için (aktifleştirmek için yorumu kaldır)
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.fleet.ledger"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fleet.ledger"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        
        // API anahtarlarını BuildConfig'e ekle
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(FileInputStream(localPropertiesFile))
        }
        
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", 
            "\"${properties.getProperty("google.maps.api.key", "")}\"")
        buildConfigField("String", "ACCOUNTING_API_KEY", 
            "\"${properties.getProperty("accounting.logo.api.key", "")}\"")
        buildConfigField("String", "EINVOICE_API_KEY", 
            "\"${properties.getProperty("einvoice.api.key", "")}\"")
        buildConfigField("String", "BANKING_API_KEY", 
            "\"${properties.getProperty("banking.api.key", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { 
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.coil.compose)
    ksp(libs.androidx.room.compiler)
    
    // WorkManager - Bildirimler için
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Biometric - Biyometrik kimlik doğrulama
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    
    // Firebase - Cloud sync ve authentication (opsiyonel)
    // implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // implementation("com.google.firebase:firebase-auth-ktx")
    // implementation("com.google.firebase:firebase-firestore-ktx")
    
    // Google Maps - Rota takibi (opsiyonel)
    // implementation("com.google.android.gms:play-services-maps:18.2.0")
    // implementation("com.google.maps.android:maps-compose:4.3.0")
    
    // PDF oluşturma
    implementation("com.itextpdf:itext7-core:7.2.5")
    
    // Excel oluşturma
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
}
