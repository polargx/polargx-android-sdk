package com.library.polargx.data.links.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.library.polargx.PolarConstants

class LinksLocalDatasourceImpl(
    private val sharedPreferences: SharedPreferences
) : LinksLocalDatasource {

    override suspend fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        sharedPreferences.edit {
            putBoolean(PolarConstants.Local.Prefers.FIRST_TIME_LAUNCH_KEY, isFirstTimeLaunch)
        }
    }

    override suspend fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean(PolarConstants.Local.Prefers.FIRST_TIME_LAUNCH_KEY, true)
//        return true
    }

}
