# PolarGX Android SDK Installation Guide

A complete guide for integrating **PolarGX SDK** into your **Kotlin** or **Java** Android app.

### 1. Create Polar project:
- [instructions/android-create-project.md](/instructions/android-create-project.md)

### 2. Configure Android Studio project
- Android Studio project: [instructions/android-configure-project.md](/instructions/android-configure-project.md)

### 3. Add PolarGX SDK
- Gradle: [instructions/android-gradle.md](/instructions/android-gradle.md)

### 4. Using PolarGX SDK
- Using the SDK in Kotlin: [instructions/android-kotlin.md](/instructions/android-kotlin.md)
- Using the SDK in Java: [instructions/android-java.md](/instructions/android-java.md)

### 5. Push Notifications
- PolarGX SDK supports push notifications via **FCM** (Firebase Cloud Messaging). The SDK automatically registers and manages push tokens for your users.

#### 5.1. Configure Push Service
- FCM: [instructions/android-push-fcm-configuration.md](/instructions/android-push-fcm-configuration.md)

**Note**: _You can create multiple push services for different environments (e.g., one for Production and one for Development). Each service should have a unique Service Name and appropriate configuration._

#### 5.2. Configure PolarGX SDK for Push Notifications
- FCM with Kotlin: [instructions/android-push-fcm-kotlin.md](/instructions/android-push-fcm-kotlin.md)
- FCM with Java: [instructions/android-push-fcm-java.md](/instructions/android-push-fcm-java.md)
