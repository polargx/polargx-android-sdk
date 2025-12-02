#### Using the SDK in Java

* Get *App Id* and *API Key* from [https://app.polargx.com](https://app.polargx.com)

#### In `Application.java` or `MainActivity.java`

```java
import com.polargx.PolarApp;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Add: Initialize Polar app
        PolarApp.initialize(
            application,
            "YOUR_APP_ID",
            "YOUR_API_KEY",
            (link, data, error) -> {
                Log.d("POLAR", "detect link clicked: " + link + ", data: " + data + ", error: " + error);
                // Handle link clicked. This callback will be called in the main thread.
            }
        );
        
        // Your existing code
    }
}
```