package com.polargx.demo_app

import android.app.Application
import android.util.Log
import com.library.polargx.PolarApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DemoApplication)
        }

        PolarApp.isLoggingEnabled = true
        PolarApp.initialize(
            application = this,
            appId = "2c0681aa-56a4-4d77-a518-6ef29af43f6c",
            apiKey = "dev_q7Pix0PNqPQQS0A6PkL17gUU1AizX3d3wmvaOD7b",
            onLinkClickHandler = { link, data, error ->
                Log.d("Polar", "\n[DEMO] detect clicked: $link, data: $data, error: $error\n")
            }
        )
    }
}