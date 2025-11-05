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
            appId = "25a9fed0-6936-47d3-8ac1-2a3a716fbbff",
            apiKey = "dev_FGmgxH9saC9lwTUIxPqvh6cRShudkDnl8uqPbbGf",
            onLinkClickHandler = { link, data, error ->
                Log.d("Polar", "\n[DEMO] detect clicked: $link, data: $data, error: $error\n")
            }
        )
    }
}