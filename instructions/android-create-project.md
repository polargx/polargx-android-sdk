#### Create and configure Polar project in PolarGX Admin Portal

* Register your PolarGX account at [https://app.polargx.com](https://app.polargx.com).
* Create your project.
* Setting your project in [Project Settings](https://app.polargx.com/app/settings)
* Manage your API Keys in [API Keys Configuration](https://app.polargx.com/configuration/api-keys-configuration)
* Configure your link domain in [Link Configuration](https://app.polargx.com/configuration/link-configuration) > Required Redirects section > Android Redirects with:
  * **Play Store URL or Custom URL**: Help your link redirects to Play Store or your custom url if your app hasn't been installed.
  * **Android Package Name**: Use your *Package Name* (e.g., `com.yourcompany.yourapp`)
  * **SHA256 Cert Fingerprints**: Look for the **SHA-256** value in the output under the signing config you're using (usually `debug` for development and `release` for production). Copy the SHA-256 fingerprint and paste it into the **SHA256 Cert Fingerprints** field in the PolarGX Admin Portal.
    
    **Note**: You'll need separate SHA-256 fingerprints for debug and release builds. Add both if you want App Links to work in both environments.
  * **Scheme URI**: Help your link opens app if your app was installed and can't be opened by *Universal Links*.
    Example: `yourapp_schemeurl://`

