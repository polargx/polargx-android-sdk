#### Install via Gradle

PolarGX Android SDK is available via **Gradle**.

1. Open your project in Android Studio.
2. Add the repository to your project-level `build.gradle` (or `settings.gradle`):

```gradle
repositories {
     maven { url = uri("https://jitpack.io") }
}
```

3. Add the dependency to your app-level `build.gradle`:

```gradle
dependencies {
    implementation '"com.github.polargx:polargx-android-sdk:latest_version'
}
```

4. Sync your project with Gradle files.

