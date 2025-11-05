package com.library.polargx.data.others.local

import android.content.Context

interface OthersLocalDatasource {
    suspend fun isFirstTimeLaunch(
        context: Context?,
        nowInMillis: Long
    ): Boolean
}
