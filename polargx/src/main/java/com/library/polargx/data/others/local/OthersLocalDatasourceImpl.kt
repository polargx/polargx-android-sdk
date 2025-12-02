package com.library.polargx.data.others.local

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.library.polargx.PolarConstants
import java.util.concurrent.TimeUnit

class OthersLocalDatasourceImpl(
    private val sharedPreferences: SharedPreferences
) : OthersLocalDatasource {
    override suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean {
        if (context == null) return false
        val firstTime =
            sharedPreferences.getBoolean(PolarConstants.Local.Prefers.FIRST_TIME_LAUNCH_KEY, true)
        if (!firstTime) return false // Already marked as not first time

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val installTimeMillis = packageInfo.firstInstallTime

            // Check if install time is stored. If not, store it.
            val storedInstallTime =
                sharedPreferences.getLong(PolarConstants.Local.Prefers.INSTALL_TIME_KEY, 0L)
            if (storedInstallTime == 0L) {
                sharedPreferences.edit()
                    .putLong(PolarConstants.Local.Prefers.INSTALL_TIME_KEY, installTimeMillis)
                    .apply()
            }

            val timeDifferenceMillis = nowInMillis - installTimeMillis
            val timeDifferenceSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)

            // If it's a very recent install (adjust threshold), it's the first launch.
            if (timeDifferenceSeconds < 60) { // Adjust threshold as needed
                sharedPreferences.edit()
                    .putBoolean(PolarConstants.Local.Prefers.FIRST_TIME_LAUNCH_KEY, false)
                    .apply() // Mark as not first time
                return true
            } else {
                //If the time difference is greater than the threshold, and the app was reinstalled, it is not the first time
                sharedPreferences.edit()
                    .putBoolean(PolarConstants.Local.Prefers.FIRST_TIME_LAUNCH_KEY, false).apply()
                return false
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false // Handle error as not first time
        }
    }
}
