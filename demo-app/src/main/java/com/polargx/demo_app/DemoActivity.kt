package com.polargx.demo_app

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.library.polargx.PolarApp
import com.library.polargx.listener.PolarInitListener

class DemoActivity : AppCompatActivity() {
    companion object {
        const val TAG = ">>>DemoActivity"
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


        PolarApp.shared.updateUser(
            userID = "ccafd507-88a7-4eb0-b9fe-100d2460dede",
            attributes = mapOf(
                "email" to "hoangnam9194+1@gmail.com",
                "username" to "Hoangnam+1",
                "fullName" to "hoangnam9194",
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
        if (uri == null) {
            fetchInstallReferrer { url ->
                PolarApp.shared.matchLinkClick(url)
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        val uri = intent.data
        PolarApp.shared.reBind(
            uri = uri,
            listener = mPolarInitListener
        )
        if (uri == null) {
            fetchInstallReferrer { url ->
                PolarApp.shared.matchLinkClick(url)
            }
        }
    }

    private fun fetchInstallReferrer(onResult: (String?) -> Unit) {
        val referrerClient = InstallReferrerClient.newBuilder(this).build()

        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer
                            onResult(referrerUrl)
                        } catch (e: Exception) {
                            onResult(null)
                        } finally {
                            referrerClient.endConnection()
                        }
                    }

                    else -> {
                        onResult(null)
                        referrerClient.endConnection()
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {

            }
        })
    }
}