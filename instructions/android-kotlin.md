#### Using the SDK in Kotlin

* Get *App Id* and *API Key* from [https://app.polargx.com](https://app.polargx.com)

#### In `Application.kt` or `MainActivity.kt`

```kotlin
// Add: Import PolarGX
import com.polargx.PolarApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Add: Initialize Polar app
        PolarApp.initialize(
            application = application,
            appId = "YOUR_APP_ID",
            apiKey = "YOUR_API_KEY"
        ) { link, data, error ->
            Log.d("POLAR", "detect link clicked: $link, data: $data, error: $error")
            // Handle link clicked. This callback will be called in the main thread.
        }
        
        // Your existing code
    }
}
```
