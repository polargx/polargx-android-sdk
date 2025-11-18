# PolarGX Android SDK --- Installation & Setup Guide

This guide walks you through installing and configuring the PolarGX SDK
for Android, including project setup, deep links, push notifications,
and initialization in your application.

## 📌 1. Create & Configure Your PolarGX Project

### **1.1. Create PolarGX Account & Project**

-   Register a new account at **https://app.polargx.com**
-   Enter your **Company Name** and **PolarGX Domain**, select **Continue** to create your project

### **1.2. Generate API Key**

Go to:

`Configurations → API Keys`

-   Click **Generate New Key** to create API key
-   Enter **Key Name** and **Key Description**
-   Choose purpose: **Mobile apps / frontend**
-   Click **Create Key**

### **1.3. Configure Web Domain**

Go to:

`Configurations → Web Domain`

In **Domain Configuration** section, select the following:

-   **PolarGX Subdomain**
-   **Custom Domain**

### **1.4. Configure Android Redirects**

Go to:

`Configurations → Link`

In **Android Redirects**, you must configure 3 types of redirects:

#### **1. Custom URL**

Used when your app is not installed (fallback to Google Play or a custom
link)\
Example: `https://play.google.com/store/apps/details?id={your.package.name}`

#### **2. App Links**

Used to open the app directly when installed

Steps:

``` bash
# Mac
./gradlew signingReport

# Windows
gradlew signingReport
```

-   Use the **SHA-256 fingerprint** from the output
-   Add it to your App Links configuration

#### **3. Scheme URI**

Used when App Links cannot open the app

Example:

    yourapp_schemeurl://

### **1.5. Configure Push Notifications**

Go to:

`Configurations → Push Services`

Steps:

1.  Click **New Push Service** to create push service
2.  In **Platform Selection**, select **Google Cloud Messaging (GCM)**
3.  In **Platform Configuration**:\
3.1.  Enter **Service Name** and **Package Name (Android) / Bundle ID (iOS)**\
3.2.  Follow **How to Configure Firebase Cloud Messaging (FCM/GCM)** instruction and upload the downloaded **JSON** file 
4.  Click **Create Push Service**

------------------------------------------------------------------------

## 📌 2. Add PolarGX SDK to Android Project

### **2.1. Add SDK Dependency**

In `build.gradle` (Module: app):

``` gradle
dependencies {
    implementation "com.github.polargx:polargx-android-sdk:v3.1.7"
}
```

> 💡 Check for the latest version on JitPack.

### **2.2. Add JitPack Repository**

In `settings.gradle`:

``` gradle
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

Then click **Sync Now** in Android Studio.

------------------------------------------------------------------------

## 📌 3. Configure AndroidManifest.xml

Add the following intent filters to the activity that will handle deep
links:

``` xml
<!-- Internal Scheme -->
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="briskbhm.testing" />
</intent-filter>

<!-- Universal Link -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" />
    <data android:host="{sub-domain}.biglittlecookies.com" />
</intent-filter>
```

------------------------------------------------------------------------

## 📌 4. Initialize and Use PolarGX SDK

### **4.1. Get Required Credentials**

Get your **App ID** and **API Key** from **https://app.polargx.com**

### **4.2. Initialize SDK in `MyApplication.kt`**

``` kotlin
override fun onCreate() {
    super.onCreate()

    PolarApp.isLoggingEnabled = true
    Polar.initialize(
        application = this,
        appId = YOUR_APP_ID,
        apiKey = YOUR_API_KEY
    )
    PolarApp.initialize(
        application = application,
        appId = BuildConfig.POLAR_APP_ID,
        apiKey = BuildConfig.POLAR_API_KEY,
        onLinkClickHandler = { link, data, error ->
            // TODO: Handle link click here
        }
    )
}
```

------------------------------------------------------------------------

### **4.3. Bind Link Handling in `MainActivity.kt`**

``` kotlin
override fun onStart() {
    super.onStart()
    PolarApp.shared.bind(
        uri = uri,
        listener = PolarInitListener { attributes, error ->
            // TODO: Handle logic here
        }
    )
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    PolarApp.shared.reBind(
        uri = uri,
        listener = PolarInitListener { attributes, error ->
            // TODO: Handle logic here
        }
    )
}
```

------------------------------------------------------------------------

## 🎉 Setup Complete!

You're ready to integrate PolarGX features such as deep links,
analytics, and push notifications.
