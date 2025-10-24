package com.polargx.demo_app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.library.polargx.Constants.PolarEventKey
import com.library.polargx.PolarApp

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        PolarApp.isLoggingEnabled = true
        PolarApp.initialize(
            application = application,
            appId = "0105f9f4-95f3-4d2d-8a58-134d4c9fec66",
            apiKey = "dev_0fSHR94kXZ4XZCitPR5B26vlwLUekwpk8w4sHSUF",
            onLinkClickHandler = { link, data, error ->
                Log.d("Polar", "\n[DEMO] detect clicked: $link, data: $data, error: $error\n")
            }
        )

        PolarApp.shared.updateUser(
            userID = "e1a3cb25-839e-4deb-95b0-2fb8ebd79401",
            attributes = mapOf(
                PolarEventKey.Name to "a",
                PolarEventKey.Email to "a@gmail.com"
            )
        )

//        PolarApp.shared.updateUser(
//            userID = "e1a3cb25-839e-4deb-95b0-2fb8ebd79402",
//            attributes = mapOf(
//                PolarEventKey.Name to "b",
//                PolarEventKey.Email to "b@gmail.com",
//                "datap1" to mapOf(
//                    "datasub1" to 1,
//                    "datasub2" to false,
//                    "datasub3" to "hele",
//                    "datasub4" to null,
//                    "datasub5" to 5f
//                )
//            )
//        )

//        PolarApp.shared.trackEvent(
//            name = "test_event",
//            attributes = mapOf(
//                "datap1" to mapOf(
//                    "datasub1" to 1,
//                    "datasub2" to false,
//                    "datasub3" to "hele",
//                    "datasub4" to null,
//                    "datasub5" to 5f
//                )
//            )
//        )
    }
}