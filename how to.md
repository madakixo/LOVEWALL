# How to Build and Run LoveWall

This guide provides instructions on how to set up, build, and run the LoveWall application.

## Prerequisites

1. **Android Studio**: Install the latest version (2024.1+ recommended).
2. **JDK**: Ensure you have JDK 17 installed and configured in your environment.
3. **Firebase Project**:
    * Create a project at [Firebase Console](https://console.firebase.google.com/).
    * Add an Android app with package name `com.lovewall.app`.
    * Download `google-services.json` and place it in the `app/` directory.

## Build Instructions

### 1. Initialize Gradle Wrapper

If the `gradlew` script is missing, you can generate it by running:

```powershell
gradle wrapper
```

*(Note: Requires `gradle` installed on your system. If you use Android Studio, it will usually handle this for you when you open the project.)*

### 2. Build the App

To build the debug APK, run:

```powershell
.\gradlew assembleDebug
```

The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

### 3. Run on Device

To install and run the app directly on a connected device:

```powershell
.\gradlew installDebug
```

## Troubleshooting

* **Command Not Recognized**: On Windows, ensure you use `.\gradlew` instead of `./gradlew` in PowerShell, or just `gradlew` in CMD.
* **Missing google-services.json**: The build will fail if this file is missing from the `app/` folder.
* **Signaling Server**: For the Eavesdrop feature, ensure the signaling server URL is correctly configured in `SignalingClient.kt`.

---

## Deployment to Production

1. **Configure Proguard**: Ensure `minifyEnabled true` is set in `app/build.gradle.kts`.
2. **Generate Signed APK**: Use Android Studio's **Build > Generate Signed Bundle / APK**.
3. **Firebase Rules**: Deploy the following security rules to Firestore:

```javascript
service cloud.firestore {
  match /databases/{database}/documents {
    match /couples/{coupleId}/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```
