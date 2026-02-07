<div align="center">

# 📱 YOLO Projekat Android
### *Moderni AI Kontrolni Centar za Autonomna Vozila*

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-38bdf8?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-075985?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/AI-Google_ML_Kit-38bdf8?style=for-the-badge&logo=googlecloud&logoColor=white)](https://developers.google.com/ml-kit)
[![License: MIT](https://img.shields.io/badge/License-MIT-94a3b8?style=for-the-badge)](https://opensource.org/licenses/MIT)

---

<p align="center">
  <b>YOLO Vozilo Android</b> pretvara tvoj mobilni uređaj u napredni terminal za upravljanje. 
  <br>Razvijena korišćenjem <b>Jetpack Compose</b>-a, aplikacija nudi besprekoran spoj performansi i modernog dizajna.
</p>



</div>

## 🚀 Ključni Moduli

### 🧠 Vizuelna Inteligencija (AI)
* **Object Tracking:** Dinamička detekcija objekata u realnom vremenu koristeći optimizovane YOLO-style modele.
* **Smart Follow Mode:** Inteligentni algoritam koji omogućava vozilu da samostalno prati cilj na osnovu analize frejmova.
* **OCR Autopilot:** Napredna ekstrakcija teksta za automatsko izvršavanje pisanih komandi sa okruženja.

### 🎮 Kontrolni Inženjering
* **Low-Latency Stream:** Optimizovan HTTP bafer za prikaz videa sa minimalnim kašnjenjem.
* **Dual-Interface Control:** Biraj između preciznog D-Pad-a za tehničke manevre ili intuitivnog džojstika za fluidnu vožnju.
* **WebSocket Core:** Asinhrona komunikacija za trenutni odziv motora vozila.

### 📼 Media & Recording
* **Hardware Acceleration:** Snimanje frejmova direktno u MP4 format koristeći procesorsku snagu uređaja.
* **Gallery Integration:** Automatsko čuvanje AI detekcija i snimaka za kasniju analizu.

---

## 🛠 Tehnološki Stack

| Komponenta | Tehnologija | Uloga |
| :--- | :--- | :--- |
| **Arhitektura** | MVVM | Čista i testabilna logika |
| **UI Framework** | Jetpack Compose | Deklarativni "Glass" interfejs |
| **Networking** | Ktor / OkHttp | Stabilna WebSocket konekcija |
| **AI Processing** | Google ML Kit | On-device Computer Vision |
| **Asinhronost** | Kotlin Coroutines | Multithreaded obrada frejmova |

---

## 🔧 Mrežna Konfiguracija

Aplikacija se oslanja na **YOLO-Server** arhitekturu:

> [!IMPORTANT]
> Proverite da li je mobilni uređaj povezan na istu lokalnu mrežu kao i Raspberry Pi 5.

* **Command Channel:** `ws://192.168.4.1:1606` (WebSocket)
* **Video Channel:** `http://192.168.4.1:1607/capture` (HTTP Stream)

---

## 🎨 Vizuelni Identitet

Dizajn aplikacije prati **Glassmorphism** principe u skladu sa web portalom:
* **Primary:** `#38bdf8` (Electric Blue)
* **Background:** `#0f172a` (Deep Space Blue)
* **Effects:** Blur efekti (12dp) na kontrolnim panelima za maksimalnu preglednost.

---

<div align="center">

**Autor:** Danilo Stoletović • **Mentor:** Dejan Batanjac  
**ETŠ „Nikola Tesla“ Niš • 2026**

</div>
