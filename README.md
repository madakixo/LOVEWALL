# LOVEWALL

markdown

# LoveWall – Interactive Couple Wallpaper App (MVP)

**LoveWall** is an Android live wallpaper application designed specifically for couples.  
It transforms your phone's home screen into a shared, always-on communication space where partners can send messages, notes, stickers, voice clips, and even share location or ambient audio — **without ever opening a separate chat app**.

The core idea: **your wallpaper becomes the chat**.

### Features (MVP Scope)

- **Interactive Live Wallpaper**  
  - Tap the wallpaper to send sticky notes, stickers, voice notes or instant messages  
  - Real-time overlays appear on your partner's wallpaper

- **Real-time Messaging**  
  - Sticky notes (text with color, expiry, draggable position)  
  - Instant text messages  
  - Voice notes (record & send short audio clips)  
  - Basic sticker support (expandable)

- **Location Sharing**  
  - Real-time partner location widget on wallpaper (with consent)  
  - Background updates (throttled to save battery)

- **Consensual Ambient Listening (Eavesdrop Mode)**  
  - Short, permission-based one-way audio streaming via WebRTC  
  - Use cases: hear surroundings for safety, surprises, or fun

- **Modern Android Architecture**  
  - Jetpack Compose UI  
  - Hilt dependency injection  
  - Coroutines + Flow for reactive updates  
  - Firestore real-time sync with date-based sharding  
  - Offline persistence enabled  
  - WebRTC peer-to-peer audio + Socket.IO signaling

### Current Status (Early 2026 MVP)

✅ Core wallpaper interactivity  
✅ Real-time message/note sync  
✅ Location sharing (background)  
✅ Basic eavesdrop via WebRTC  
✅ Onboarding screen (Compose)  
✅ Optimized rendering & battery usage  

🚧 Missing / To-do for production-ready MVP:  
- Full Firebase Authentication + real pairing (QR or code)  
- Runtime permission requests & rationale dialogs  
- Voice recording UI & playback controls  
- Sticker pack selector & custom themes  
- Proper error handling & offline indicators  
- Firebase security rules (only paired users)  
- Analytics events & crash reporting polish  
- Beta testing distribution setup

### Tech Stack

| Category            | Tools / Libraries                              |
|---------------------|------------------------------------------------|
| Language            | Kotlin 100%                                    |
| UI                  | Jetpack Compose + Material 3                   |
| Dependency Injection| Hilt                                           |
| Async / Reactive    | Kotlin Coroutines + Flow                       |
| Backend             | Firebase (Auth, Firestore, Storage, Crashlytics) |
| Real-time Audio     | WebRTC (google-webrtc)                         |
| Signaling           | Socket.IO client → Node.js server (separate)   |
| Location            | Google Play Services Fused Location            |
| Testing             | JUnit 5, MockK, coroutines-test, Compose UI tests |
| Architecture        | MVVM + Repository pattern                      |

### Getting Started (Development)

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/lovewall.git
   cd lovewall

Open in Android Studio
Use Android Studio 2024.2+ (Koala or later recommended)
Add Firebase configurationCreate a Firebase project at https://console.firebase.google.com
Add Android app → download google-services.json
Place it in app/ folder

Enable Firebase servicesAuthentication (Email/Password or Phone)
Firestore Database
Cloud Storage
Crashlytics (optional but recommended)

Deploy minimal signaling server (required for eavesdrop feature)
See signaling-server/ folder or the Node.js skeleton below.
Run the appConnect an Android 10+ device or emulator
./gradlew installDebug
Or press Run in Android Studio

Test on two devicesTemporarily hardcode same coupleId in code for testing
Set wallpaper on both phones
Send notes → watch them appear in real-time

Firebase Security Rules (Minimum Safe Example)firestore

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /couples/{coupleId}/{document=**} {
      allow read, write: if request.auth != null &&
                           request.auth.uid in coupleId.split('-');
    }
  }
}

Copy into Firebase console → Firestore → Rules.Signaling Server (Minimal Socket.IO – Node.js)Create a separate repo or folder /signaling-server:server.jsjavascript

const express = require('express');
const { createServer } = require('http');
const { Server } = require('socket.io');

const app = express();
const server = createServer(app);
const io = new Server(server, { cors: { origin: "*" } });

const rooms = new Map(); // roomId → [socket ids]

io.on('connection', (socket) => {
  console.log('Client connected:', socket.id);

  socket.on('join', (roomId) => {
    socket.join(roomId);
    if (!rooms.has(roomId)) rooms.set(roomId, []);
    rooms.get(roomId).push(socket.id);
  });

  socket.on('offer', (data) => io.to(data.roomId).emit('offer', data));
  socket.on('answer', (data) => io.to(data.roomId).emit('answer', data));
  socket.on('ice-candidate', (data) => io.to(data.roomId).emit('ice-candidate', data));

  socket.on('disconnect', () => {
    console.log('Client disconnected:', socket.id);
  });
});

app.get('/', (req, res) => res.send('LoveWall Signaling Server'));

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => console.log(`Signaling server running on port ${PORT}`));

package.jsonjson

{
  "name": "lovewall-signaling",
  "version": "1.0.0",
  "main": "server.js",
  "dependencies": {
    "express": "^4.18.2",
    "socket.io": "^4.7.2"
  },
  "scripts": {
    "start": "node server.js"
  }
}

Deploy to Google Cloud Run, Render.com, Railway.app, or Fly.io (free tier sufficient for MVP).Build & Test Commandsbash

# Unit tests
./gradlew test

# Instrumented (UI) tests
./gradlew connectedAndroidTest

# Build debug APK
./gradlew assembleDebug

# Build signed release APK
./gradlew assembleRelease

Contributing / Next StepsWant to help?
Priority tasks right now:Implement Firebase Auth + real pairing flow  
Add runtime permission launcher in onboarding  
Build voice recording UI (MediaRecorder)  
Create sticker picker screen  
Deploy & connect signaling server  
Write proper Firestore rules & test security

Feel free to open issues or PRs!Made with  by jayymadd clicke ltd

If you want to add sections (e.g., screenshots, license, contributing guide, roadmap, or make it shorter), le

