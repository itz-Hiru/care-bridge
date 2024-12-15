# 🌉 Care Bridge 

## 🚀 Community Donation & Sharing Platform

![GitHub stars](https://img.shields.io/github/stars/itz-Hiru/care-bridge?style=social)
![GitHub last commit](https://img.shields.io/github/last-commit/itz-Hiru/care-bridge)
![Android Build](https://img.shields.io/badge/Android-Build-green?logo=android)

### 📖 Overview

Care Bridge is a mobile application designed to foster community support by enabling seamless donation and request of essential items like food, books, and other necessities.

### 🗂 Project Structure

```
Carebridge/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/carebridge/
│   │   │   │   ├── activities/
│   │   │   │   │
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── CarousalAdapter.java
│   │   │   │   │   ├── DonationAdapter.java
│   │   │   │   │   ├── FoodViewPagerAdapter.java
│   │   │   │   │   ├── NavigationViewPagerAdapter.java
│   │   │   │   │   └── NotificationAdapter.java
│   │   │   │   │
│   │   │   │   ├── models/
│   │   │   │   │   ├── Donation.java
│   │   │   │   │   └── Notification.java
│   │   │   │   │
│   │   │   │   └── fragments/
│   │   │   │       ├── HomeFragment.java
│   │   │   │       ├── NotificationFragment.java
│   │   │   │       └── SettingsFragment.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   └── menu/
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/
│   │       └── java/
│   │           └── com/carebridge/
│   │               └── UnitTests.java
│   │
│   ├── libs/
│   └── build.gradle
│
├── gradle/
│   └── wrapper/
│
├── .gitignore
├── build.gradle
├── google-services.json (not tracked)
└── README.md
```

### 🏗 Structure Explanation

#### 📁 Main Packages
- `activities/`: Core app screens and user interactions
- `adapters/`: RecyclerView adapters for lists
- `models/`: Data models representing app entities
- `fragments/`: Modular UI components

#### 🔑 Key Components
- User Authentication
- Donation Management
- Request Tracking
- Profile Management

### ✨ Key Features

- 🎁 Item Donation Platform
- 📋 Item Request System
- 🔐 Secure User Authentication
- 🔔 Real-time Notifications

### 📚 Libraries & Technologies

#### 🔍 Core Libraries
- **Navigation**: AAndroidx ViewPager Navigation Library
- **QR Code**: Zixin QR Code Generator/Scanner
- **Chart Visualization**: MP Android Charts
- **Photo Manipulation**: Piccasso Library

#### 🔥 Firebase Integration
- Firebase Authentication
- Firebase Realtime Database
- Firebase Cloud Messaging

### 🛠 Tech Stack
- **Language**: Java
- **IDE**: Android Studio
- **Backend**: Firebase
- **Architecture**: MVVM

### 📦 Dependencies

Add these to your `build.gradle`:

```groovy
dependencies {
    implementation 'com.google.zxing:core:3.4.1'
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
    implementation 'androidx.viewpager2:viewpager2:1.1.0'
    implementation 'com.github.PhilJay:MPAndroidChart:3.1.0'
    implementation 'com.github.dhaval2404:imagepicker:2.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    implementation 'com.squareup.picasso:picasso:2.71828'
}
```

### 🚀 Quick Start

#### Prerequisites
- Android Studio
- Java JDK 11+
- Firebase Account

#### Installation Steps
1. Clone the repository
   ```bash
   git clone https://github.com/itz-Hiru/care-bridge.git

2. Firebase Setup
   - Create project in Firebase Console
   - Download `google-services.json`
   - Place in `app/` directory

3. Configure Zixin Libraries
   - Add library dependencies
   - Initialize in `MainActivity`

### 🔒 Security Features
- Firebase Authentication
- Secure QR Code Generation
- Data Encryption
- Role-Based Access Control

### 🤝 Contributing
1. Fork the Project
2. Create Feature Branch
3. Commit Changes
4. Push to Branch
5. Open Pull Request

### 📝 License
Distributed under [Your License]. See `LICENSE` for more information.

### 📞 Contact
- Your Name: itz-Hiru
- Project Link: https://github.com/itz-Hiru/care-bridge
- Email: hirumithakuladewanew@gmail.com

---

**Made with ❤️ for Community Sharing**
