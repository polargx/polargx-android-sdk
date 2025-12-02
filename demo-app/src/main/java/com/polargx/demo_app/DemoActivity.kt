package com.polargx.demo_app

import android.app.ComponentCaller
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.library.polargx.PolarApp
import com.library.polargx.PolarConstants
import com.library.polargx.listener.PolarInitListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DemoActivity : AppCompatActivity() {
    companion object {
        const val TAG = ">>>PolarDemoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        CoroutineScope(Dispatchers.IO).launch {
            PolarApp.shared.updateUser(
                userID = "ccafd507-88a7-4eb0-b9fe-100d2460dede",
                attributes = mapOf(
                    PolarConstants.PolarEventKey.Name to "nhn.gl03",
                    PolarConstants.PolarEventKey.Email to "nhn.gl03@gmail.com",
                    "datap1" to mapOf(
                        "datasub1" to 1,
                        "datasub2" to false,
                        "datasub3" to "hele",
                        "datasub4" to null,
                        "datasub5" to 2f
                    )
                )
            )

            PolarApp.shared.setGCM(fcmToken = "fcm_token_test")
        }
        registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Log.d(TAG, "onReceive: action=${intent?.action}")
                    when (intent?.action) {
                        PolarConstants.RATE_LIMIT_WAITED_ACTION -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(500)
                                PolarApp.shared.updateUser(
                                    userID = "ccafd507-88a7-4eb0-b9fe-100d2460dede",
                                    attributes = mapOf(
                                        PolarConstants.PolarEventKey.Name to "nhn.gl03",
                                        PolarConstants.PolarEventKey.Email to "nhn.gl03@gmail.com",
                                        "datap1" to mapOf(
                                            "datasub1" to 1,
                                            "datasub2" to false,
                                            "datasub3" to "update user while waiting for rate limit",
                                            "datasub4" to null,
                                            "datasub5" to 2f
                                        )
                                    )
                                )
//                                PolarApp.shared.trackEvent(
//                                    name = "test_event",
//                                    attributes = mapOf(
//                                        "datap1" to mapOf(
//                                            "datasub1" to 1,
//                                            "datasub2" to false,
//                                            "datasub3" to "send while waiting for rate limit",
//                                            "datasub4" to null,
//                                            "datasub5" to 5f
//                                        )
//                                    )
//                                )
                            }
                        }
                    }
                }
            },
            IntentFilter().apply {
                addAction(PolarConstants.RATE_LIMIT_WAITED_ACTION)

            },
            Context.RECEIVER_EXPORTED
        )

//        for (i in 1..1) {
//            CoroutineScope(Dispatchers.IO).launch {
//                PolarApp.shared.trackEvent(
//                    name = "test_event",
//                    attributes = mapOf(
//                        "datap1" to mapOf(
//                            "datasub1" to 1,
//                            "datasub2" to false,
//                            "datasub3" to "hele",
//                            "datasub4" to null,
//                            "datasub5" to 5f
//                        )
//                    )
//                )
//            }
//        }
    }

    private val mPolarInitListener = object : PolarInitListener {
        override fun onInitFinished(
            attributes: Map<String, Any?>?,
            error: Throwable?
        ) {
            Log.d(TAG, "onInitFinished: attributes=$attributes, error=$error")
        }
    }

    override fun onStart() {
        super.onStart()
        val uri = intent?.data
        PolarApp.shared.bind(
            uri = uri,
            listener = mPolarInitListener
        )
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        val uri = intent.data
        PolarApp.shared.reBind(
            uri = uri,
            listener = mPolarInitListener
        )
    }
}