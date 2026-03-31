# FleetLedger Kurulum Rehberi

Bu rehber, FleetLedger uygulamasının tüm özelliklerini aktif hale getirmek için gerekli adımları içerir.

## 📋 İçindekiler
1. [Firebase Kurulumu](#1-firebase-kurulumu)
2. [Google Maps API](#2-google-maps-api)
3. [API Anahtarları Yapılandırması](#3-api-anahtarları-yapılandırması)
4. [Bağımlılıkları Aktifleştirme](#4-bağımlılıkları-aktifleştirme)

---

## 1. Firebase Kurulumu

### Adım 1.1: Firebase Projesi Oluştur
1. [Firebase Console](https://console.firebase.google.com/) adresine git
2. "Proje Ekle" butonuna tıkla
3. Proje adı: `FleetLedger` (veya istediğin bir isim)
4. Google Analytics'i etkinleştir (opsiyonel)
5. Projeyi oluştur

### Adım 1.2: Android Uygulaması Ekle
1. Firebase Console'da projenize girin
2. Android simgesine tıklayın
3. Paket adı: `com.fleet.ledger`
4. Uygulama takma adı: `FleetLedger`
5. SHA-1 imza sertifikası (Debug için):
   ```bash
   cd FleetLedger
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   Çıktıdaki SHA-1 değerini kopyala ve Firebase'e yapıştır

### Adım 1.3: google-services.json İndir
1. Firebase'den `google-services.json` dosyasını indir
2. Dosyayı şu konuma kopyala:
   ```
   FleetLedger/app/google-services.json
   ```

### Adım 1.4: Firebase Authentication Aktifleştir
1. Firebase Console → Authentication → Get Started
2. Sign-in method sekmesine git
3. Email/Password'ü etkinleştir
4. (Opsiyonel) Google Sign-In'i etkinleştir

### Adım 1.5: Firestore Database Oluştur
1. Firebase Console → Firestore Database → Create Database
2. Production mode'u seç
3. Konum: `europe-west1` (veya size yakın bir bölge)
4. Rules sekmesine git ve şu kuralları ekle:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId}/{document=**} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /vehicles/{vehicleId} {
         allow read, write: if request.auth != null;
       }
       match /trips/{tripId} {
         allow read, write: if request.auth != null;
       }
       match /partners/{partnerId} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

### Adım 1.6: build.gradle.kts Güncelle
`FleetLedger/build.gradle.kts` dosyasını aç ve Firebase plugin'ini ekle:
```kotlin
plugins {
    // ... mevcut pluginler
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

`FleetLedger/app/build.gradle.kts` dosyasını aç ve şu satırları aktifleştir:
```kotlin
plugins {
    // ... mevcut pluginler
    id("com.google.gms.google-services")
}

dependencies {
    // ... mevcut bağımlılıklar
    
    // Firebase - Yorumları kaldır
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}
```

---

## 2. Google Maps API

### Adım 2.1: Google Cloud Console
1. [Google Cloud Console](https://console.cloud.google.com/) adresine git
2. Firebase projenizi seçin (otomatik olarak oluşturulmuştur)
3. "APIs & Services" → "Library" sekmesine git
4. "Maps SDK for Android" ara ve etkinleştir
5. "Places API" ara ve etkinleştir (opsiyonel, adres arama için)

### Adım 2.2: API Key Oluştur
1. "APIs & Services" → "Credentials" sekmesine git
2. "Create Credentials" → "API Key" seç
3. API Key'i kopyala
4. "Restrict Key" butonuna tıkla:
   - Application restrictions: Android apps
   - Add an item: Paket adı `com.fleet.ledger` ve SHA-1 sertifikanı ekle
   - API restrictions: "Restrict key" seç
   - "Maps SDK for Android" ve "Places API" seç
5. Kaydet

### Adım 2.3: API Key'i Ekle
`FleetLedger/app/src/main/AndroidManifest.xml` dosyasını aç ve `<application>` tagı içine ekle:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="BURAYA_API_KEY_YAPISTIR" />
```

### Adım 2.4: build.gradle.kts Güncelle
`FleetLedger/app/build.gradle.kts` dosyasında Google Maps bağımlılıklarını aktifleştir:
```kotlin
dependencies {
    // ... mevcut bağımlılıklar
    
    // Google Maps - Yorumları kaldır
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")
}
```

---

## 3. API Anahtarları Yapılandırması

### Adım 3.1: local.properties Dosyası Oluştur
Güvenlik için API anahtarlarını `local.properties` dosyasında sakla (Git'e eklenmez):

`FleetLedger/local.properties` dosyasını oluştur veya aç:
```properties
# Firebase
firebase.api.key=BURAYA_FIREBASE_API_KEY

# Google Maps
google.maps.api.key=BURAYA_GOOGLE_MAPS_API_KEY

# Muhasebe Entegrasyonu (Opsiyonel)
accounting.logo.api.key=BURAYA_LOGO_API_KEY
accounting.logo.company.code=BURAYA_FIRMA_KODU

# E-Fatura (Opsiyonel)
einvoice.api.key=BURAYA_GIB_API_KEY
einvoice.username=BURAYA_KULLANICI_ADI
einvoice.password=BURAYA_SIFRE

# Banka API (Opsiyonel)
banking.api.key=BURAYA_BANKA_API_KEY
banking.account.number=BURAYA_HESAP_NO
```

### Adım 3.2: build.gradle.kts'de API Anahtarlarını Oku
`FleetLedger/app/build.gradle.kts` dosyasına ekle:
```kotlin
android {
    // ... mevcut ayarlar
    
    defaultConfig {
        // ... mevcut ayarlar
        
        // API anahtarlarını BuildConfig'e ekle
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
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
    
    buildFeatures {
        buildConfig = true
    }
}
```

---

## 4. Bağımlılıkları Aktifleştirme

### Adım 4.1: Gradle Sync
1. Android Studio'da "File" → "Sync Project with Gradle Files"
2. Hataları kontrol et ve düzelt

### Adım 4.2: İzinleri Test Et
Uygulamayı çalıştır ve şu izinleri test et:
- Kamera (fotoğraf çekme)
- Konum (rota takibi)
- Biyometrik (parmak izi)

---

## 5. Test ve Doğrulama

### Firebase Test
1. Uygulamayı çalıştır
2. Kayıt ol ekranından yeni kullanıcı oluştur
3. Firebase Console → Authentication'da kullanıcıyı gör
4. Firestore'da veri oluştur ve senkronizasyonu test et

### Google Maps Test
1. Rotalar ekranına git
2. Harita görünümünü aç
3. Konum izni ver
4. Haritanın yüklendiğini doğrula

### Widget Test
1. Ana ekrana git
2. Uzun bas → Widgets
3. FleetLedger widget'ını ekle
4. Verilerin göründüğünü kontrol et

---

## 🚨 Önemli Notlar

1. **Güvenlik**: `local.properties` ve `google-services.json` dosyalarını Git'e ekleme!
2. **SHA-1**: Release build için ayrı SHA-1 sertifikası gerekir
3. **Faturalandırma**: Google Maps API kullanımı için kredi kartı gerekebilir (ücretsiz kota var)
4. **Test**: Önce emülatörde test et, sonra gerçek cihazda dene

---

## 📞 Destek

Sorun yaşarsan:
1. Firebase Console'da hata loglarını kontrol et
2. Android Studio Logcat'i incele
3. Build hatalarını oku ve çöz

## 🎉 Tamamlandı!

Tüm adımları tamamladıktan sonra uygulamanın tüm özellikleri aktif olacak!
