# Digital Tasbeeh Counter

An advanced, production-grade Android Islamic prayer counter built with Jetpack Compose (Material 3), Room Database, Hardware Sensory Engines, and Firebase Firestore Cloud Sync.

---

## Features
- **Core Counter Engine**: Full-screen hit-target, tactile haptics & SoundPool feedback, atomic round rollovers (33/99/100/custom), lifecycle-aware session timer, and hardware volume key capture.
- **4 Native Canvas Skins**:
  1. *Minimal Ring Theme* (Emerald #0D3B3E / Gold #F5A623 glowing vector gradient)
  2. *Classic LCD Theme* (Matte green #A3C1AD simulated 7-segment digital font with 3D mechanical push button)
  3. *Tally Counter Theme* (Mechanical chrome/beige roller dials)
  4. *Misbaha Prayer Beads Track* (Dynamic translation along cord with physics & ambient glow)
- **Granular Settings & Localization**: Multi-language support (English, Uzbek, Arabic, Russian) and WorkManager daily 06:00 AM notification reminder.
- **Offline-First Cloud Sync**: Room Database with relation mappings + Firebase Firestore bidirectional synchronization adhering to the *Last-Write-Wins* conflict resolution rule.

---

## Cloud Build & APK Generation via GitHub Actions
This repository is configured with .github/workflows/android.yml to automatically build the APK in the cloud without requiring a local Android SDK.

### How to Build & Download APK:
1. Push this project to your GitHub repository:
   `ash
   git init
   git add .
   git commit -m "Initial commit: Digital Tasbeeh Counter"
   git remote add origin https://github.com/<YOUR_USERNAME>/<YOUR_REPO_NAME>.git
   git branch -M main
   git push -u origin main
   `
2. Navigate to the **Actions** tab on your GitHub repository.
3. The **Android CI Build & APK Release** workflow will run automatically.
4. Once completed (approx. 2 minutes), scroll to the **Artifacts** section at the bottom of the workflow run summary to download DigitalTasbeeh-Debug-APK.zip containing the installable APK file, or check the **Releases** section for the direct .apk binary.

---

## Firebase Configuration Setup
A placeholder google-services.json is included so the project compiles cleanly out-of-the-box. To enable real Firestore Sync and Google Sign-In:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a project and add an Android App with package name com.tasbeeh.digital.
3. Download the generated google-services.json.
4. Place it inside the pp/ directory (replacing pp/google-services.json).
5. In the Firebase Console, enable **Firestore Database** and **Firebase Authentication** (Google Sign-In).
