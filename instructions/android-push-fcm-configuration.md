#### For FCM (Firebase Cloud Messaging)

Follow these detailed steps to obtain the required information from Firebase Console. You'll need a **Firebase project**.

**Step 1: Access Firebase Console**

1. Visit [https://console.firebase.google.com](https://console.firebase.google.com) and sign in with your Google account
2. Create a new Firebase project or select an existing one
3. Make sure you have **Owner** or **Editor** access to configure the project

**Step 2: Add Android App to Firebase**

1. Click **Add app** and select **Android**
2. Enter your Android package name (e.g., `com.yourcompany.yourapp`)
3. Enter an app nickname (optional)
4. Click **Register app**

**Step 3: Download Configuration File**

1. Download the `google-services.json` file
2. Place it in your Android app's `app/` directory (same level as `build.gradle`)
3. This file contains your Firebase configuration

**Step 4: Get GCM service account file**

1. Enable Firebase Cloud Messaging API
Go to Firebase Console and select your project. Ensure **Firebase Cloud Messaging API (V1)** is enabled. You can check the status on the **Status Dashboard** link shown in your project settings.
2. In Firebase Console, go to **Project Settings** (⚙️ gear icon)You'll see your Sender ID displayed - note this for reference.
3. Navigate to the **Service Accounts**
In Project settings, go to the **Service accounts** tab. Here you'll see your service account email and options to manage it. Click on **Manage service accounts permissions** link to open **Google Cloud Console**.
4. Access Service Account Keys
In the **Google Cloud Console**, find your Firebase Admin SDK service account (format: **firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com**). Click on it to view details, then go to the **Keys** tab at the top.
5. Create and Download JSON Key
Click **Add key** → **Create new key**. Select **JSON** as the **key type (recommended)** and click **Create**. A JSON file containing your private key will be automatically downloaded.

**⚠️ Important**: _Store this file securely - it can't be recovered if lost!_

**Step 5: Upload FCM Credentials to PolarGX**

1. Go to [https://app.polargx.com](https://app.polargx.com)
2. Navigate to **CONFIGURATIONS > Push Services**
3. Click **+ New Push Service** and select **Google Cloud Messaging (GCM)**
4. Fill in the required information:
   * **Service Name**: Enter a descriptive name
   * **Package Name**: Enter your Android app's package name (from Step 2)
   * **Upload your GCM service account file**:  Upload GCM service account file (from step 4)

5. Click **Create Push Service**

**💡 Pro Tip**: _Firebase Cloud Messaging (FCM) supports sending notifications to both Android and iOS devices using a single configuration. You can register multiple apps (Android & iOS) under the same Firebase project and reuse the same service account credentials._

**⚠️ Security Warning**: _Service account keys grant full access to your Firebase project. Never commit them to version control, share them publicly, or embed them in client-side code. Use the Workload Identity Google Cloud feature or rotate keys regularly for production environments._

