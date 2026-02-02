# 🚗 YOLO projekat android
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-green.svg)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/AI-Google_ML_Kit-orange.svg)](https://developers.google.com/ml-kit)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**YOLO Vozilo** je moderna Android aplikacija razvijena u Kotlinu (Jetpack Compose) koja služi kao kontrolni centar za pametno vozilo bazirano na mikrokontrolerima (poput ESP32 ili Raspberry Pi). Aplikacija kombinuje daljinsko upravljanje u realnom vremenu sa naprednim AI funkcijama za prepoznavanje objekata i teksta.



---

## ✨ Ključne Karakteristike

### 📺 Live Stream & AI Monitoring
* **Live Stream Monitoring:** Prikaz video signala sa kamere vozila u realnom vremenu preko HTTP protokola.
* **AI Prepoznavanje Objekata (YOLO-style):** Implementacija *Google ML Kit Object Detection* za identifikaciju i praćenje objekata na ekranu.
* **Smart Follow Mode:** Automatsko praćenje detektovanog objekta (vozilo se okreće i kreće ka objektu na osnovu pozicije u kadru).
* **OCR & Auto-Pilot:** Prepoznavanje pisanih komandi ("napred", "levo", "back", itd.) direktno sa slike i njihovo automatsko izvršavanje.

### 🎮 Kontrolni Sistem
* **Dual Control System:**
    * *Compact D-Pad:* Klasične strelice za precizno kretanje.
    * *Circular Joystick:* Intuitivni džojstik za fluidno upravljanje.
* **WebSocket Komunikacija:** Brz prenos komandi kretanja bez latencije.

### 📸 Multimedia & Recording
* **Snimanje i Slikanje:** Čuvanje fotografija direktno u galeriju telefona.
* **Nativno MP4 snimanje:** Konvertovanje niza frejmova u video fajl direktno na uređaju pomoću hardverskog kodiranja.

---

## 🛠 Tehnologije

| Segment | Tehnologija |
| :--- | :--- |
| **UI Framework** | Jetpack Compose (Moderni deklarativni UI) |
| **AI/ML Engine** | Google ML Kit (Object Detection & Text Recognition) |
| **Networking** | OkHttp & WebSockets |
| **Image Loading** | Coil (Efikasno učitavanje frejmova) |
| **Video Processing** | MediaCodec & MediaMuxer |

---

## 🚀 Kako radi?

### 1. Povezivanje
Aplikacija pokušava da se poveže na vozilo putem dve adrese:
* **WebSocket:** `ws://192.168.4.1:1606` (za slanje komandi kretanja).
* **HTTP Stream:** `http://192.168.4.1:1607/capture` (za preuzimanje frejmova kamere).

### 2. Komande kretanja
Vozilo prima sledeće string komande preko WebSocketa:
* `napred`, `nazad`, `levo`, `desno`
* `rot_levo`, `rot_desno`
* `stop` (šalje se čim korisnik pusti dugme)

### 3. AI Logika
* **Follow Mode:** Aplikacija analizira `boundingBox` detektovanog objekta. Ako je objekat na levoj strani frejma, šalje se komanda `levo`, ako je u centru `napred`, a ako je desno `desno`.
* **OCR Auto-Pilot:** Ako je aktiviran, aplikacija skenira tekst. Na primer, ako vidi reč "Left", automatski šalje komandu za skretanje ulevo.

---

## 📦 Instalacija i Podešavanje

1. **Klonirajte repozitorijum:**
   ```bash
   git clone [https://github.com/danilo-stoletovic/yolo-vozilo-android.git](https://github.com/danilo-stoletovic/yolo-vozilo-android.git)
Otvorite projekat: U Android Studiju (verzija Koala ili noviji).

Konfiguracija: Dodajte neophodne dozvole u AndroidManifest.xml (Internet, Kamera, Storage).

Povezivanje: Povežite svoj telefon na Wi-Fi pristupnu tačku vozila (default IP: 192.168.4.1).

Pokrenite aplikaciju.

🎨 Teme i UI
Aplikacija koristi čistu, modernu paletu boja:

🔵 ThemeBlue (#3498DB): Primarna boja kontrola.

🟢 ThemeSuccess (#2ECC71): Indikator aktivnog AI moda.

🔴 ThemeAlert (#E74C3C): Indikator snimanja i diskonekcije.

Autor: Danilo Stoletovic

Licenca: MIT License
