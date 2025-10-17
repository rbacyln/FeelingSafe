# FeelingSafe

FeelingSafe is a lightweight Android app that helps users quickly reach trusted contacts and local emergency services. Open the app → trigger SOS → share location and notify selected contacts.

> Built as a student project to practice Android fundamentals (Activities, Intents, permissions, RecyclerView, Room) and clean app structure.

---

## ✨ Features

- **One-tap SOS:** Trigger an SMS or call to a primary emergency contact.
- **Location sharing:** Attach current GPS coordinates (if permission granted).
- **Trusted contacts:** Add, edit, and remove contacts stored locally.
- **Quick actions:** Call local emergency numbers (e.g., 112) from the home screen.
- **Offline-friendly:** Core actions work even without internet (SMS/Calls).
- **Privacy-first:** Data stays on device; no external servers.

---

## 📸 Screenshots

Create a `/screenshots` folder and add images.

| Home | Contacts | SOS Confirmation |
| --- | --- | --- |
| ![Home](screenshots/home.png) | ![Contacts](screenshots/contacts.png) | ![SOS](screenshots/sos.png) |

---

## 🏗️ Tech Stack

- **Android** (min/target SDK: _fill in_)
- **Java** for app source
- **Gradle** (Kotlin DSL scripts)
- **Room** for local storage (contacts)
- **RecyclerView** for lists
- **Location APIs** (Fused Location Provider or Android Location)

---

## 📂 Project Structure (high-level)

app/
└─ src/
├─ main/
│ ├─ java/.../feelingsafe/
│ │ ├─ ui/ # Activities / Fragments / Adapters
│ │ ├─ data/ # Room DB, DAO, entities (Contact)
│ │ ├─ domain/ # Models, use-cases (if used)
│ │ └─ util/ # Helpers (permissions, location)
│ └─ res/ # Layouts, drawables, strings
├─ build.gradle.kts
settings.gradle.kts




> Adjust folder names to match your package.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (latest stable)
- **Android SDK** and an emulator or a physical device (Android _x.y_+)

### Setup
1. **Clone**
   ```bash
   git clone https://github.com/rbacyln/FeelingSafe.git
   cd FeelingSafe
Open in Android Studio → File > Open… → project root.
Sync Gradle when prompted.
Run on an emulator or a device (USB debugging on).
🔐 Permissions
The app may request:
CALL_PHONE – place emergency calls
SEND_SMS – send SOS messages
ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION – attach GPS coordinates
READ_CONTACTS / WRITE_CONTACTS (optional) – import or manage contacts
On Android 6.0+ permissions are requested at runtime. The app should gracefully handle denial (e.g., show a toast and continue with limited functionality).
⚙️ Configuration
Default emergency number: set in res/values/strings.xml (e.g., 112)
SOS message template: add a format string, e.g.
"I need help. My location: https://maps.google.com/?q=%1$s,%2$s"
Location fallback: if GPS is unavailable, send SMS without coordinates
🧪 Manual Test Checklist
Launch app → try SOS without permissions → verify friendly prompts
Grant Location → confirm SMS includes a Google Maps link
Add contacts → check RecyclerView updates, edit/delete flows
Deny SMS/Call → app should not crash; show guidance
🗺️ Roadmap
 Share via other apps (WhatsApp, Telegram, etc.)
 In-app map preview of current location
 Panic widget / Quick Settings tile
 Import contacts from phone
 Multi-language support (EN/TR)
 Basic unit tests and instrumentation tests
🧱 Architecture Notes
Simple MVVM-ish layout:
UI (Activity/Fragment) observes state and triggers actions
ViewModel holds state and coordinates use-cases
Repository abstracts data sources (Room, location provider)
Data layer with Room (Entity Contact, ContactDao)
🤝 Contributing
Fork and create a feature branch:
git checkout -b feature/my-change
Commit with clear messages and open a PR.
Include a short description, screenshots (if UI), and test notes.
📄 License
Add a license file (e.g., MIT) at the repo root and mention it here.
🙋‍♀️ Contact
Rabia Ceylan
GitHub: https://github.com/rbacyln
LinkedIn: https://www.linkedin.com/in/rabia-ceylan-080966218/
::contentReference[oaicite:0]{index=0}
