#### Configure App Links:

* In Android Studio, open your `AndroidManifest.xml` file.
* Add an `<intent-filter>` to your main activity with the following configuration:

```xml
<activity android:name=".MainActivity">
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="https"
            android:host="{subdomain}.gxlnk.com" />
    </intent-filter>
</activity>
```

#### Configure Custom Scheme:

* In Android Studio, open your `AndroidManifest.xml` file.
* Add an `<intent-filter>` to your main activity with the following configuration:

```xml
<activity android:name=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="yourapp_schemeurl" />
    </intent-filter>
</activity>
```

