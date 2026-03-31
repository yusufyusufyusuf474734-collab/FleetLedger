# 🚀 FleetLedger - Hızlı Başlangıç

## Minimum Kurulum (Temel Özellikler)

Uygulamayı hemen çalıştırmak için:

### 1. Projeyi Aç
```bash
cd FleetLedger
```

### 2. Gradle Sync
Android Studio'da:
- File → Sync Project with Gradle Files

### 3. Çalıştır
- Run → Run 'app'

**Not:** Firebase, Google Maps ve diğer entegrasyonlar olmadan da uygulama çalışır. Bu özellikler opsiyoneldir.

---

## Temel Özellikler (Kurulum Gerektirmez)

✅ Araç yönetimi
✅ Sefer takibi
✅ Gelir/gider yönetimi
✅ Ortak yönetimi
✅ Belge takibi
✅ Raporlar ve grafikler
✅ Bakım takibi
✅ Yakıt yönetimi
✅ PDF/Excel export
✅ Fotoğraf ekleme
✅ Bildirimler

---

## Gelişmiş Özellikler (Kurulum Gerektirir)

### Firebase (Cloud Sync & Multi-User)
📖 Detaylı kurulum: [SETUP_GUIDE.md](SETUP_GUIDE.md#1-firebase-kurulumu)

**Hızlı Adımlar:**
1. Firebase Console'da proje oluştur
2. `google-services.json` indir → `app/` klasörüne kopyala
3. `build.gradle.kts` dosyalarındaki Firebase yorumlarını kaldır
4. Gradle Sync

### Google Maps (Rota Takibi)
📖 Detaylı kurulum: [SETUP_GUIDE.md](SETUP_GUIDE.md#2-google-maps-api)

**Hızlı Adımlar:**
1. Google Cloud Console'da API key al
2. `AndroidManifest.xml`'e API key ekle
3. `build.gradle.kts`'deki Maps yorumlarını kaldır
4. Gradle Sync

### Biyometrik Güvenlik
✅ Zaten aktif! Cihazınızda parmak izi/yüz tanıma varsa çalışır.

---

## Proje Yapısı

```
FleetLedger/
├── app/
│   ├── src/main/
│   │   ├── java/com/fleet/ledger/
│   │   │   ├── core/              # Temel altyapı
│   │   │   │   ├── auth/          # Kimlik doğrulama
│   │   │   │   ├── data/          # Veritabanı
│   │   │   │   ├── domain/        # İş mantığı
│   │   │   │   ├── export/        # PDF/Excel
│   │   │   │   ├── integration/   # Dış entegrasyonlar
│   │   │   │   ├── notification/  # Bildirimler
│   │   │   │   ├── security/      # Biyometrik
│   │   │   │   └── sync/          # Cloud sync
│   │   │   ├── feature/           # Ekranlar
│   │   │   │   ├── auth/          # Giriş/Kayıt
│   │   │   │   ├── dashboard/     # Ana sayfa
│   │   │   │   ├── vehicle/       # Araçlar
│   │   │   │   ├── trip/          # Seferler
│   │   │   │   ├── partner/       # Ortaklar
│   │   │   │   ├── document/      # Belgeler
│   │   │   │   ├── report/        # Raporlar
│   │   │   │   ├── maintenance/   # Bakım
│   │   │   │   ├── fuel/          # Yakıt
│   │   │   │   ├── route/         # Rotalar
│   │   │   │   ├── search/        # Arama
│   │   │   │   ├── share/         # Hisse yönetimi
│   │   │   │   └── settings/      # Ayarlar
│   │   │   ├── ui/                # UI bileşenleri
│   │   │   └── widget/            # Ana ekran widget
│   │   └── res/                   # Kaynaklar
│   └── build.gradle.kts
├── SETUP_GUIDE.md                 # Detaylı kurulum rehberi
├── QUICK_START.md                 # Bu dosya
└── local.properties.example       # API key şablonu
```

---

## Önemli Dosyalar

### Yapılandırma
- `local.properties` - API anahtarları (Git'e eklenmez)
- `google-services.json` - Firebase yapılandırması (Git'e eklenmez)
- `AndroidManifest.xml` - İzinler ve servisler

### Kod
- `MainActivity.kt` - Uygulama giriş noktası
- `FleetLedgerApp.kt` - Ana navigasyon
- `FleetDatabase.kt` - Room veritabanı

---

## Sık Sorulan Sorular

### Firebase olmadan çalışır mı?
✅ Evet! Tüm temel özellikler local veritabanı ile çalışır.

### Google Maps olmadan çalışır mı?
✅ Evet! Rota ekranı placeholder gösterir, diğer özellikler çalışır.

### Hangi Android sürümlerini destekler?
📱 Android 8.0 (API 26) ve üzeri

### Emülatörde test edebilir miyim?
✅ Evet! Tüm özellikler emülatörde çalışır.

### Biyometrik nasıl test edilir?
Emülatörde: Settings → Security → Fingerprint → Add fingerprint

---

## Yardım

Sorun mu yaşıyorsun?

1. **Build hatası**: Gradle Sync yap
2. **Import hatası**: File → Invalidate Caches / Restart
3. **Runtime hatası**: Logcat'i kontrol et
4. **Firebase hatası**: `google-services.json` doğru konumda mı?
5. **Maps hatası**: API key doğru mu? Manifest'te tanımlı mı?

---

## Sonraki Adımlar

1. ✅ Uygulamayı çalıştır ve test et
2. 📖 [SETUP_GUIDE.md](SETUP_GUIDE.md) oku
3. 🔥 Firebase'i kur (opsiyonel)
4. 🗺️ Google Maps'i kur (opsiyonel)
5. 🚀 Uygulamayı yayınla!

---

**İyi kodlamalar! 🎉**
