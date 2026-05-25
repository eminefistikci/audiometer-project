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
    │   │   ├── ControlPanel.java        ← Frekans / dB / kulak kontrolü ve otomatik test modu
    │   │   ├── LogPanel.java            ← Sistem logları ve eşik kayıt tablosu
    │   │   ├── ModernButton.java        ← Özel modern UI buton bileşeni
    │   │   └── RoundedBorder.java       ← Yuvarlatılmış kart kenarlıkları
    │
    │   ├── functional/
    │   │   ├── AudiometryRules.java     ← Saf medikal doğrulama ve odyometri kuralları
    │   │   ├── ResponseProcessor.java   ← RESPONSE mesajlarını stream/map/filter ile işleme
    │   │   ├── Maybe.java               ← Functional Optional/Maybe implementasyonu
    │   │   └── ValidationResult.java    ← Yan etkisiz doğrulama sonuç modeli
    │
    │   └── algorithm/
    │       ├── HughsonWestlakeStep.java   ← Immutable Hughson-Westlake algoritma adımı
    │       └── HughsonWestlakeEngine.java ← Saf fonksiyon tabanlı test algoritması motoru
    │
    └── test/java/com/audiometer/
        │
        ├── functional/
        │   ├── AudiometryRulesTest.java      ← Medikal kural doğrulama testleri
        │   └── ResponseProcessorTest.java    ← RESPONSE processing testleri
        │
        └── algorithm/
            ├── HughsonWestlakeEngineTest.java    ← Algoritma davranış testleri
            └── HughsonWestlakePropertyTest.java  ← Property-based rastgele doğrulama testleri
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

## Software Engineering Features

- Functional programming based medical rule engine
- Immutable data structures for audiometry sessions
- Hughson-Westlake automatic threshold algorithm
- Stream API based RESPONSE message processing
- Optional / Maybe pattern for safe error handling
- Unit testing with JUnit 5
- Property-based randomized validation tests
- Automatic threshold test mode integrated into GUI

## Software Architecture

The project follows a hybrid Object-Oriented Programming (OOP) and Functional Programming (FP) architecture.

- GUI and device communication layers are implemented using OOP principles.
- Medical validation logic and audiometry algorithms are implemented using pure functional components.
- Immutable models and side-effect free processing were preferred for testability and reliability.

## Automated Testing

The project includes:

- Unit tests for audiometry rules
- Response processing validation tests
- Hughson-Westlake algorithm tests
- Property-based randomized tests for IEC-compatible constraints

Current status:

- Total tests: 18
- Failures: 0
- Errors: 0


## Automatic Threshold Test Mode

The GUI includes an automatic Hughson-Westlake testing mode.

The system automatically:

1. Sends tones through the serial interface
2. Waits for patient RESPONSE messages
3. Decreases intensity by 10 dB after response
4. Increases intensity by 5 dB after no response

This feature simulates semi-automatic audiometric threshold detection.
