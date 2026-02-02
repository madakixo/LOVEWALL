# LoveWall Deployment Guide

This document outlines the steps required to deploy the LoveWall application to production, including backend setup and infrastructure configuration.

## Backend Setup (Firebase)

1. **Project Creation**:
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. **Enable Services**:
    - **Authentication**: Enable Email/Password or Phone authentication.
    - **Cloud Firestore**: Initialize in production mode. Apply the security rules provided below.
    - **Cloud Storage**: Initialize to store voice notes and sticker assets.
3. **Security Rules (Firestore)**:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /couples/{coupleId}/{document=**} {
      allow read, write: if request.auth != null &&
                           request.auth.uid in coupleId.split('-');
    }
  }
}
```

## Signaling Server (WebRTC)

The eavesdropping feature requires a Socket.IO signaling server.

1. **Deploy the Server**:
    - Use the Node.js skeleton provided in `README.md`.
    - Deploy to a platform like Google Cloud Run, Heroku, or Render.
2. **Update App Configuration**:
    - Update the `SIGNALING_SERVER_URL` in `com.lovewall.app.webrtc.SignalingClient`.

## Google Maps API (Location Widget)

1. **GCP Console**: Create a project in the [Google Cloud Console](https://console.cloud.google.com/).
2. **Enable APIs**: Enable the **Maps SDK for Android**.
3. **API Key**: Generate an API key and add it to `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

## Building the App

1. **Add Configuration**: Place your `google-services.json` in the `app/` directory.
2. **Build Release APK**:
    - Open the project in Android Studio.
    - Go to **Build > Generate Signed Bundle / APK**.
    - Follow the wizard to sign your APK with a release key.
3. **Alternative (Command Line)**:

    ```powershell
    .\gradlew assembleRelease
    ```

## Post-Deployment

1. **Testing**: Verify the pairing flow and real-time wallpaper updates on physical devices.
2. **Monitoring**: Monitor [Firebase Crashlytics](https://console.firebase.google.com/u/0/project/_/crashlytics) for any runtime issues.
