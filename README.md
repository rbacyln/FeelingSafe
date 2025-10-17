````markdown
# 🚨 FeelingSafe (Android)

![Platform](https://img.shields.io/badge/platform-Android-brightgreen)
![Language](https://img.shields.io/badge/language-Java-red)
![Storage](https://img.shields.io/badge/storage-Room-blue)
![UI](https://img.shields.io/badge/UI-RecyclerView-lightgrey)
![Location](https://img.shields.io/badge/location-GPS%2FFused--Location-informational)

## 🔎 Overview
FeelingSafe is a lightweight Android app for quick SOS. With one tap, you can place an emergency call or send an SMS (optionally with a Google Maps location link) to a trusted contact. Contacts are stored locally; no external servers.

---

## 📁 Files
- `app/src/main/java/.../feelingsafe/`
  - `ui/` – Activities/Adapters (home, contacts, SOS flow)
  - `data/` – Room DB, DAOs, `Contact` entity
  - `util/` – helpers (permissions, location, SMS/call)
- `app/src/main/res/` – layouts, drawables, strings  
- `build.gradle.kts`, `settings.gradle.kts` – Gradle config

---

## 📝 Notes
- **Core features:** one-tap SOS (call/SMS), location link in SMS, local trusted contacts, 112 quick call.
- **Offline:** calls/SMS work without internet.
- **Privacy:** data stays on device.
- **SDK:** minSdk: _fill_, targetSdk: _fill_.
- **Architecture:** simple MVVM-ish (UI → ViewModel → Repository → Room).

---

## ▶️ Run the App
```bash
git clone https://github.com/rbacyln/FeelingSafe.git
cd FeelingSafe
````

Open in **Android Studio** → let Gradle sync → **Run** on emulator or device.

---

## 🔐 Permissions

* `CALL_PHONE` – emergency calls
* `SEND_SMS` – SOS messages
* `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` – attach GPS

<!-- Remove if unused:
- `READ_CONTACTS` / `WRITE_CONTACTS` – import/manage contacts
-->

Android 6.0+ uses runtime permissions; the app should degrade gracefully if denied.

---

## ⚙️ Configure

* Default emergency number: `res/values/strings.xml` (e.g., `112`)
* SOS template example: `I need help. My location: https://maps.google.com/?q=%1$s,%2$s`

---

## 📸 Screenshots (optional)

Put images in `/screenshots`:
`home.png`, `contacts.png`, `sos.png`

---

## 👤 Contact

**Rabia Ceylan** · GitHub: [https://github.com/rbacyln](https://github.com/rbacyln) · LinkedIn: [https://www.linkedin.com/in/rabia-ceylan-080966218/](https://www.linkedin.com/in/rabia-ceylan-080966218/)

```
::contentReference[oaicite:0]{index=0}
```
