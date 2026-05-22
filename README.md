<div align="center">

# 🌊 MindFlow – Code Repository

<img src="https://img.shields.io/badge/Android-App-green?style=for-the-badge">
<img src="https://img.shields.io/badge/Type-Diary%20App-blue?style=for-the-badge">
<img src="https://img.shields.io/badge/Status-In%20Development-orange?style=for-the-badge">

</div>

---

## 🧠 About MindFlow

MindFlow is a modern Android diary app designed to help users **capture thoughts instantly and securely**.

It focuses on:
- ⚡ Speed (write in seconds)
- 🔒 Privacy-first design
- 🧘 Minimal distraction UI
- 📝 Smooth journaling experience

> “Your thoughts deserve a clean space to flow.”

---

## 🚀 Run Locally

### 📌 Prerequisites
- Android Studio (latest recommended)
- Android SDK installed
- Emulator or physical device

---

### 🛠️ Setup Steps

#### 1️⃣ Open Project

Open Android Studio → Click "Open" → Select project folder
2️⃣ Let Gradle Sync

Android Studio will automatically:

Download dependencies
Fix compatibility issues
Setup project structure
3️⃣ Create Environment File 🔐

Create a file in the root directory:

.env

Add your Gemini API key:

GEMINI_API_KEY=your_api_key_here

👉 Refer to .env.example for format reference.

4️⃣ Fix Signing (Important)

In app/build.gradle.kts, remove:

signingConfig = signingConfigs.getByName("debugConfig")

This is only needed for release builds.

5️⃣ Run the App 🚀

Now simply:

Run ▶️ on Emulator or Physical Device
⚠️ Notes
🔑 Never commit .env or API keys
🧪 Debug builds are for testing only
📦 Release builds are required for production use
🌊 MindFlow Philosophy
<div align="center"> <img src="https://readme-typing-svg.herokuapp.com?font=Orbitron&size=22&duration=3000&color=00F7FF&center=true&vCenter=true&width=600&lines=Thoughts+come+fast;MindFlow+keeps+them+safe;Write+without+distraction"> </div>
📌 Project Status

🚧 Actively evolving
✨ New features in development
📱 Android-first experience

<div align="center">
⭐ If you like this project, consider starring the repo!
</div> ```
