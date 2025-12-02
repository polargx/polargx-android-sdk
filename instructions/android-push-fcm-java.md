#### Configure FCM with Java

**Step 1: Add Firebase to Your Project**

1. Add the Google services plugin to your project-level `build.gradle`:

```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}
```

2. Apply the plugin in your app-level `build.gradle`:

```gradle
apply plugin: 'com.google.gms.google-services'
```

3. Add Firebase dependencies:

```gradle
dependencies {
    implementation 'com.google.firebase:firebase-messaging:23.3.0'
}
```

**Step 2: Create a Firebase Messaging Service**

Create a new file `MyFirebaseMessagingService.java`:

```java
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.polargx.PolarApp;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Register the FCM token with PolarGX
        PolarApp.shared.setGCM(token);
    }
}
```

**Step 3: Register the Service in AndroidManifest.xml**

```xml
<service
    android:name=".MyFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

**Step 4: Initialize PolarGX with Push Service**

In your `MainActivity.java`:

```java
import com.polargx.PolarApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PolarApp.shared.bind(
            uri = uri,
            listener = PolarInitListener { attributes, error ->
                // TODO: Handle logic here
            }
        )
        // Your existing code
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // Add: Handle incoming intent
        PolarApp.shared.reBind(
            uri = uri,
            listener = PolarInitListener { attributes, error ->
                // TODO: Handle logic here
            }
        )
    }
}
```