# 🔌 API Entegrasyon Özeti

## Nasıl Yapacağım? - Adım Adım Kılavuz

### 📱 1. Firebase Kurulumu (Cloud Sync & Multi-User)

#### Ne İşe Yarar?
- Kullanıcı girişi ve kayıt
- Verileri bulutta saklama
- Çoklu cihaz senkronizasyonu
- Kullanıcılar arası veri paylaşımı

#### Nasıl Yapılır?

**Adım 1: Firebase Projesi Oluştur**
1. Tarayıcıda [console.firebase.google.com](https://console.firebase.google.com) aç
2. "Proje Ekle" butonuna tıkla
3. Proje adı yaz: `FleetLedger`
4. "Devam Et" → "Devam Et" → "Projeyi Oluştur"

**Adım 2: Android Uygulaması Ekle**
1. Firebase Console'da Android simgesine tıkla
2. Paket adı: `com.fleet.ledger` yaz
3. "Uygulamayı Kaydet"

**Adım 3: google-services.json İndir**
1. "google-services.json indir" butonuna tıkla
2. İndirilen dosyayı şu klasöre taşı:
   ```
   FleetLedger/app/google-services.json
   ```

**Adım 4: Authentication Aç**
1. Sol menüden "Authentication" tıkla
2. "Get Started" butonuna tıkla
3. "Email/Password" seç → Etkinleştir → Kaydet

**Adım 5: Firestore Aç**
1. Sol menüden "Firestore Database" tıkla
2. "Create Database" butonuna tıkla
3. "Production mode" seç → "Next"
4. Konum seç: `europe-west1` → "Enable"

**Adım 6: Kodu Aktifleştir**
1. `FleetLedger/build.gradle.kts` dosyasını aç
2. Bu satırın yorumunu kaldır:
   ```kotlin
   // id("com.google.gms.google-services") version "4.4.0" apply false
   ```
   Şöyle olmalı:
   ```kotlin
   id("com.google.gms.google-services") version "4.4.0" apply false
   ```

3. `FleetLedger/app/build.gradle.kts` dosyasını aç
4. Bu satırların yorumunu kaldır:
   ```kotlin
   // id("com.google.gms.google-services")
   
   // Firebase
   // implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
   // implementation("com.google.firebase:firebase-auth-ktx")
   // implementation("com.google.firebase:firebase-firestore-ktx")
   ```

5. Android Studio'da: File → Sync Project with Gradle Files

✅ **Tamamlandı!** Artık kullanıcı girişi ve cloud sync çalışıyor.

---

### 🗺️ 2. Google Maps Kurulumu (Rota Takibi)

#### Ne İşe Yarar?
- Sefer rotalarını haritada gösterme
- Mesafe hesaplama
- Konum takibi

#### Nasıl Yapılır?

**Adım 1: Google Cloud Console**
1. [console.cloud.google.com](https://console.cloud.google.com) aç
2. Firebase projenizi seçin (otomatik oluşturulmuş)

**Adım 2: Maps API Aç**
1. Sol menüden "APIs & Services" → "Library"
2. "Maps SDK for Android" ara → Tıkla → "Enable"

**Adım 3: API Key Oluştur**
1. "APIs & Services" → "Credentials"
2. "Create Credentials" → "API Key"
3. API Key'i kopyala (örn: `AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXX`)

**Adım 4: API Key'i Ekle**
1. `FleetLedger/app/src/main/AndroidManifest.xml` dosyasını aç
2. `<application>` tagı içine ekle:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXX" />
   ```

**Adım 5: Kodu Aktifleştir**
1. `FleetLedger/app/build.gradle.kts` dosyasını aç
2. Bu satırların yorumunu kaldır:
   ```kotlin
   // implementation("com.google.android.gms:play-services-maps:18.2.0")
   // implementation("com.google.maps.android:maps-compose:4.3.0")
   ```

3. Android Studio'da: File → Sync Project with Gradle Files

✅ **Tamamlandı!** Artık harita özelliği çalışıyor.

---

### 🔒 3. Biyometrik Güvenlik (Zaten Aktif!)

#### Ne İşe Yarar?
- Parmak izi ile giriş
- Yüz tanıma ile giriş

#### Nasıl Test Edilir?

**Emülatörde:**
1. Emülatör ayarlarını aç
2. Settings → Security → Fingerprint
3. Parmak izi ekle
4. Uygulamada biyometrik giriş butonuna tıkla

**Gerçek Cihazda:**
1. Cihazınızda parmak izi/yüz tanıma ayarlı olmalı
2. Uygulama otomatik algılar

✅ **Ek kurulum gerektirmez!**

---

### 💼 4. Muhasebe Entegrasyonu (Opsiyonel)

#### Ne İşe Yarar?
- Logo, Mikro, Netsis gibi muhasebe yazılımlarına veri aktarma
- Fatura ve gider senkronizasyonu

#### Nasıl Yapılır?

**Adım 1: API Key Al**
1. Muhasebe yazılımı sağlayıcınızdan API key isteyin
2. Firma kodunuzu öğrenin

**Adım 2: Yapılandır**
1. `FleetLedger/local.properties` dosyasını oluştur (yoksa)
2. Ekle:
   ```properties
   accounting.logo.api.key=BURAYA_API_KEY
   accounting.logo.company.code=BURAYA_FIRMA_KODU
   ```

**Adım 3: Kodu Tamamla**
1. `AccountingIntegration.kt` dosyasındaki TODO'ları implement et
2. API dokümantasyonuna göre entegrasyon yap

---

### 📄 5. E-Fatura Entegrasyonu (Opsiyonel)

#### Ne İşe Yarar?
- GİB e-Fatura sistemine otomatik fatura gönderme
- Fatura durumu sorgulama

#### Nasıl Yapılır?

**Adım 1: GİB Başvurusu**
1. e-Fatura sistemine kayıt olun
2. API erişimi için başvurun
3. Kullanıcı adı ve şifre alın

**Adım 2: Yapılandır**
1. `FleetLedger/local.properties` dosyasına ekle:
   ```properties
   einvoice.username=BURAYA_KULLANICI_ADI
   einvoice.password=BURAYA_SIFRE
   ```

**Adım 3: Kodu Tamamla**
1. `EInvoiceIntegration.kt` dosyasındaki TODO'ları implement et
2. GİB API dokümantasyonuna göre entegrasyon yap

---

### 🏦 6. Banka API Entegrasyonu (Opsiyonel)

#### Ne İşe Yarar?
- Banka hesap hareketlerini otomatik çekme
- Bakiye sorgulama
- Otomatik gelir/gider kaydı

#### Nasıl Yapılır?

**Adım 1: Açık Bankacılık**
1. Bankanızın açık bankacılık hizmetine başvurun
2. API key alın

**Adım 2: Yapılandır**
1. `FleetLedger/local.properties` dosyasına ekle:
   ```properties
   banking.api.key=BURAYA_API_KEY
   banking.account.number=BURAYA_HESAP_NO
   ```

**Adım 3: Kodu Tamamla**
1. `BankingIntegration.kt` dosyasındaki TODO'ları implement et
2. Banka API dokümantasyonuna göre entegrasyon yap

---

## 🎯 Öncelik Sırası

### Hemen Yapılabilir (Kolay)
1. ✅ **Firebase** - 15 dakika
2. ✅ **Google Maps** - 10 dakika
3. ✅ **Biyometrik** - Zaten aktif!

### Daha Sonra Yapılabilir (Orta)
4. 📄 **E-Fatura** - Başvuru süreci var
5. 💼 **Muhasebe** - Yazılım sağlayıcısına bağlı
6. 🏦 **Banka API** - Başvuru süreci var

---

## 🆘 Sorun Giderme

### Firebase Hatası
**Hata:** "google-services.json bulunamadı"
**Çözüm:** Dosyanın `FleetLedger/app/` klasöründe olduğundan emin ol

### Google Maps Hatası
**Hata:** "Harita yüklenmiyor"
**Çözüm:** 
1. API key doğru mu?
2. Maps SDK for Android etkin mi?
3. AndroidManifest.xml'de tanımlı mı?

### Build Hatası
**Hata:** "Sync failed"
**Çözüm:** 
1. File → Invalidate Caches / Restart
2. Gradle Sync tekrar dene

---

## 📞 Yardım

Daha fazla detay için:
- 📖 [SETUP_GUIDE.md](SETUP_GUIDE.md) - Detaylı kurulum
- 🚀 [QUICK_START.md](QUICK_START.md) - Hızlı başlangıç

---

**Başarılar! 🎉**
