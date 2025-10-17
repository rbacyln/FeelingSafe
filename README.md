
# FeelingSafe

FeelingSafe is a lightweight Android app that helps users quickly reach trusted contacts and local emergency services. Open the app → trigger SOS → share location and notify selected contacts.

Built as a student project to practice Android fundamentals (Activities, Intents, permissions, RecyclerView, Room) and clean app structure.

---

## ✨ Features

- **One-tap SOS:** Trigger an SMS or call to a primary emergency contact.
- **Location sharing:** Include a Google Maps link with current GPS (if permission granted).
- **Trusted contacts:** Add, edit, and remove contacts stored locally.
- **Quick actions:** Call local emergency numbers (e.g., 112) from the home screen.
- **Offline-friendly:** Calls/SMS work without internet.
- **Privacy-first:** Data stays on device; no external servers.

---

## 📸 Screenshots

Create a `/screenshots` folder and add images.

| Home | Contacts | SOS Confirmation |
| --- | --- | --- |
| ![Home](screenshots/home.png) | ![Contacts](screenshots/contacts.png) | ![SOS](screenshots/sos.png) |

---

## 🏗️ Tech Stack

- **Android** (minSdk: <!-- TODO: fill -->, targetSdk: <!-- TODO: fill -->)
- **Java** for app source
- **Gradle** (Kotlin DSL)
- **Room** (local storage)
- **RecyclerView** (lists)
- **Location APIs** (Fused Location Provider or Android Location)

---

## 📂 Project Structure (high-level)

```text
app/
└─ src/
   └─ main/
      ├─ java/.../feelingsafe/
      │  ├─ ui/        # Activities / Fragments / Adapters
      │  ├─ data/      # Room DB, DAO, entities (Contact)
      │  ├─ domain/    # Models, use-cases (if used)
      │  └─ util/      # Helpers (permissions, location)
      └─ res/          # Layouts, drawables, strings
build.gradle.kts
settings.gradle.kts
````

---

## 🚀 Getting Started

### Prerequisites

* **Android Studio** (latest stable)
* **Android SDK** and an emulator or a physical device (Android <!-- TODO: version -->+)

### Setup

```bash
git clone https://github.com/rbacyln/FeelingSafe.git
cd FeelingSafe
```

Open in **Android Studio** → *File > Open…* → project root → let Gradle sync → **Run** (USB debugging on).

---

## 🔐 Permissions

The app may request:

* `CALL_PHONE` – place emergency calls
* `SEND_SMS` – send SOS messages
* `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` – attach GPS coordinates

<!-- Remove if not used:
- `READ_CONTACTS` / `WRITE_CONTACTS` – import or manage contacts
-->

Android 6.0+ uses runtime permissions; the app should continue with limited functionality if denied.

---

## ⚙️ Configuration

* **Default emergency number:** `res/values/strings.xml` (e.g., `112`)
* **SOS message template:**
  `I need help. My location: https://maps.google.com/?q=%1$s,%2$s`
* **Location fallback:** if GPS is unavailable, send SMS without coordinates

---

## 🧪 Manual Test Checklist

* Trigger **SOS** without permissions → check friendly prompts
* Grant **Location** → SMS includes Google Maps link
* Add contacts → verify list updates, edit/delete flows
* Deny **SMS/Call** → no crash; show guidance

---

## 🗺️ Roadmap

* [ ] Share via other apps (WhatsApp, Telegram, etc.)
* [ ] In-app map preview of current location
* [ ] Panic widget / Quick Settings tile
* [ ] Multi-language support (EN/TR)
* [ ] Basic unit tests and instrumentation tests

---

## 🙋‍♀️ Contact

**Rabia Ceylan**
GitHub: [https://github.com/rbacyln](https://github.com/rbacyln)
LinkedIn: [https://www.linkedin.com/in/rabia-ceylan-080966218/](https://www.linkedin.com/in/rabia-ceylan-080966218/)

