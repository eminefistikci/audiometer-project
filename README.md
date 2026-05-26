# Audiometer System — Kurulum ve Çalıştırma Kılavuzu

## Proje Yapısı

```
audiometer-project/
├── pom.xml                          ← Maven bağımlılık ve build yönetimi
├── README.md                        ← Proje dokümantasyonu
│
└── src/
    ├── main/java/com/audiometer/
    │
    │   ├── AudiometerApp.java           ← Uygulama başlangıç noktası (main)
    │
    │   ├── model/
    │   │   ├── ThresholdPoint.java      ← Tek odyogram ölçüm noktası (immutable veri modeli)
    │   │   └── TestSession.java         ← Test oturumu yönetimi + Observer pattern
    │
    │   ├── serial/
    │   │   └── SerialManager.java       ← jSerialComm tabanlı seri port iletişimi
    │
    │   ├── audiogram/
    │   │   └── AudiogramPanel.java      ← Gerçek zamanlı odyogram çizimi (Swing JPanel)
    │
    │   ├── gui/
    │   │   ├── MainWindow.java          ← Ana pencere ve genel layout yönetimi
    │   │   ├── ConnectionPanel.java     ← COM port bağlantı yönetimi
    │   │   ├── ControlPanel.java        ← Manuel + otomatik Hughson-Westlake test kontrol paneli
    │   │   ├── LogPanel.java            ← Sistem logları ve threshold kayıt tablosu
    │   │   ├── ModernButton.java        ← Özel modern UI buton bileşeni
    │   │   └── RoundedBorder.java       ← Yuvarlatılmış kart kenarlıkları
    │
    │   ├── functional/
    │   │   ├── AudiometryRules.java     ← Saf medikal doğrulama ve IEC uyumlu odyometri kuralları
    │   │   ├── ResponseProcessor.java   ← RESPONSE mesajlarını stream/map/filter ile işleme
    │   │   ├── Maybe.java               ← Functional Optional/Maybe implementasyonu
    │   │   └── ValidationResult.java    ← Yan etkisiz doğrulama sonuç modeli
    │
    │   └── algorithm/
    │       ├── HughsonWestlakeStep.java   ← Immutable Hughson-Westlake algoritma adımı
    │       ├── HughsonWestlakeEngine.java ← Saf fonksiyon tabanlı test algoritması motoru
    │       ├── ThresholdEvaluation.java   ← Immutable threshold değerlendirme modeli
    │       ├── ThresholdDetector.java     ← 2/3 RESPONSE threshold tespit motoru
    │       └── FrequencyCycle.java        ← Klinik frekans sırası yönetimi
    │
    └── test/java/com/audiometer/
        │
        ├── functional/
        │   ├── AudiometryRulesTest.java      ← Medikal kural doğrulama testleri
        │   └── ResponseProcessorTest.java    ← RESPONSE processing testleri
        │
        └── algorithm/
            ├── HughsonWestlakeEngineTest.java      ← Hughson-Westlake algoritma davranış testleri
            ├── HughsonWestlakePropertyTest.java    ← Property-based rastgele doğrulama testleri
            ├── ThresholdDetectorTest.java          ← Threshold detection doğrulama testleri
            └── FrequencyCycleTest.java             ← Klinik frekans sırası testleri
│
└── target/
    └── audiometer-gui-1.0.0-jar-with-dependencies.jar
```

## Gereksinimler

- Java 11 veya üzeri
- Maven 3.6+
- jSerialComm (Maven tarafından otomatik indirilir)

## Derleme ve Çalıştırma

```bash
# Proje dizinine gir
cd audiometer-project

# Derle ve bağımlılıkları indir
mvn clean package

# Çalıştır
java -jar target/audiometer-gui-1.0.0-jar-with-dependencies.jar
```

## Proteus ile Entegrasyon

1. Proteus'ta simülasyonu başlat
2. COMPIM'in kullandığı COM port numarasını not et (örn: COM4)
3. Java uygulamasında bağlantı panelinden o portu seç ve "Bağlan" a bas
4. Artık "Ton Gönder" butonu Arduino'ya komut gönderebilir

## Komut Protokolü

| Yön           | Format                    | Örnek                  |
|---------------|---------------------------|------------------------|
| Java → Arduino | `FREQ:<hz>,INT:<db>\n`   | `FREQ:1000,INT:40\n`  |
| Java → Arduino | `STOP\n`                 | Tonu durdur            |
| Arduino → Java | `RESPONSE\n`             | Hasta butona bastı     |

## Odyogram Sembolleri (IEC 60645-1)

- **Sağ kulak**: Kırmızı "O" (çember)
- **Sol kulak**: Mavi "X"

## Clinical Hughson-Westlake Algorithm

The automatic test mode follows the clinical frequency order:

1000 Hz → 2000 Hz → 4000 Hz → 8000 Hz → 500 Hz → 250 Hz

For each frequency, the test starts at 30 dB HL.

Rules:
- If RESPONSE is received, intensity decreases by 10 dB.
- If no RESPONSE is received within the listening window, intensity increases by 5 dB.
- Threshold is accepted when the patient responds at least 2 times out of 3 trials at the same dB level.
- After threshold detection, the system automatically proceeds to the next frequency.
