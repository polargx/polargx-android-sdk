package com.library.polargx.helpers

import android.content.Context
import com.library.polargx.extension.getAppVersion
import com.library.polargx.extension.getDeviceModel
import com.library.polargx.extension.getOsVersion

object SystemInfo {
    fun getTrackingDeviceInfo(context: Context?): Map<String, Any?>? {
        if (context == null) return null
        return mapOf(
            "OSName" to "Android",
            "OSVersion" to context.getOsVersion(),
            "model" to context.getDeviceModel(),
            "SDKVersion" to context.getOsVersion(),
            "appVersion" to context.getAppVersion(),
        )
    }
}