<div align="center">

# 📱 YOLO Projekat Android
### *Moderni AI Kontrolni Centar za Autonomna Vozila*

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-38bdf8?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-075985?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![WebRTC](https://img.shields.io/badge/Stream-WebRTC-33AAFF?style=for-the-badge&logo=webrtc&logoColor=white)](https://webrtc.org/)
[![ML Kit](https://img.shields.io/badge/AI-Google_ML_Kit-38bdf8?style=for-the-badge&logo=googlecloud&logoColor=white)](https://developers.google.com/ml-kit)
[![License: MIT](https://img.shields.io/badge/License-MIT-94a3b8?style=for-the-badge)](https://opensource.org/licenses/MIT)

---

<p align="center">
  <b>YOLO Vozilo Android</b> pretvara tvoj mobilni uređaj u napredni terminal za upravljanje. 
  <br>Razvijena korišćenjem <b>Jetpack Compose</b>-a, uz <b>WebRTC</b> i <b>UDP</b> protokole, aplikacija nudi besprekoran spoj performansi, nulte latencije i modernog dizajna.
</p>

</div>

## 🚀 Ključni Moduli

### 🧠 Vizuelna Inteligencija (AI)
* **Object Tracking:** Dinamička detekcija objekata u realnom vremenu koristeći **YOLO (You Only Look Once)** logiku implementiranu kroz optimizovane ML Kit modele nad WebRTC frejmovima.
* **Smart Follow Mode:** Inteligentni algoritam koji omogućava vozilu da samostalno prati cilj na osnovu analize P2P video strima.
* **OCR Autopilot:** Napredna ekstrakcija teksta (`Google ML Kit`) za automatsko izvršavanje pisanih komandi iz okruženja.

### 🎮 Kontrolni Inženjering
* **WebRTC P2P Stream:** Hardverski ubrzan video prenos koji eliminiše baferovanje i omogućava čist real-time feed na 15+ FPS.
* **Zero-Latency UDP:** Komunikacija sa motorima se odvija preko `DatagramSocket`-a, čime se preskače handshake overhead i omogućava momentalni odziv.
* **Dual-Interface Control:** Biraj između preciznog D-Pad-a za tehničke manevre ili intuitivnog džojstika za fluidnu vožnju.

### 📼 Media & Recording
* **Native MP4 Encoding:** Snimanje WebRTC frejmova direktno u MP4 format koristeći `MediaCodec` i `MediaMuxer` (hardverska akceleracija).
* **JNI Memory Management:** Stroga kontrola C++ pointera kroz WebRTC `I420Buffer` i `retain/release` ciklus za stabilan rad bez curenja memorije.
* **16KB Page Alignment:** Potpuna optimizacija NDK i JNI biblioteka za kompatibilnost sa Android 15+ uređajima i novim CPU arhitekturama.

---

## 🛠 Tehnološki Stack

| Komponenta | Tehnologija | Uloga |
| :--- | :--- | :--- |
| **Arhitektura** | MVVM | Čista i testabilna logika |
| **UI Framework** | Jetpack Compose | Deklarativni "Glass" interfejs |
| **Networking** | WebRTC / UDP / OkHttp | P2P Video, brze komande i SDP Signaling |
| **AI Processing** | Google ML Kit (YOLO) | On-device Computer Vision detekcija |
| **Asinhronost** | Kotlin Coroutines | Multithreaded obrada i mrežni pozivi |

---

## 🔧 Mrežna Konfiguracija



Aplikacija komunicira sa Raspberry Pi serverom kroz hibridnu mrežnu arhitekturu:

> [!IMPORTANT]
> Proverite da li je mobilni uređaj povezan na istu lokalnu mrežu (WiFi Hotspot) kao i Raspberry Pi.

* **UDP Command Channel:** `192.168.4.1:1606` (Datagram paketi za motore)
* **WebRTC Signaling:** `http://192.168.4.1:1607/offer` (HTTP POST za razmenu SDP protokola)
* **Video Channel:** Direktna Peer-to-Peer WebRTC konekcija nakon uspostavljanja signala.

---

## 🎨 Vizuelni Identitet

Dizajn aplikacije prati **Glassmorphism** principe u skladu sa web portalom:
* **Primary:** `#3498DB` (Theme Blue)
* **Success/Active:** `#2ECC71` (Theme Success)
* **Alert/Recording:** `#E74C3C` (Theme Alert)
* **Background:** Adaptivni Dark/Light mod (`#121212` / `#FDFDFD`)

---

<div align="center">

**Autor:** Danilo Stoletović • **Mentor:** Dejan Batanjac  
**ETŠ „Nikola Tesla“ Niš • 2026**

</div>