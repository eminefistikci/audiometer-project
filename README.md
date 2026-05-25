# Audiometer System — Kurulum ve Çalıştırma Kılavuzu

## Proje Yapısı

```
audiometer-project/
├── pom.xml                          ← Maven bağımlılık dosyası
└── src/main/java/com/audiometer/
    ├── AudiometerApp.java           ← Başlangıç noktası (main)
    ├── model/
    │   ├── ThresholdPoint.java      ← Tek ölçüm noktası (immutable)
    │   └── TestSession.java         ← Test oturumu verisi + Observer pattern
    ├── serial/
    │   └── SerialManager.java       ← jSerialComm seri port iletişimi
    ├── audiogram/
    │   └── AudiogramPanel.java      ← Odyogram grafiği (Swing JPanel)
    └── gui/
        ├── MainWindow.java          ← Ana pencere ve layout
        ├── ConnectionPanel.java     ← COM port bağlantı yönetimi
        ├── ControlPanel.java        ← Frekans / dB / kulak seçimi ve butonlar
        ├── LogPanel.java            ← Eşik tablosu + sistem logu
        ├── ModernButton.java        ← Özel tasarlanmış düz/oval buton
        └── RoundedBorder.java       ← Kartlar için yuvarlatılmış kenarlık
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
