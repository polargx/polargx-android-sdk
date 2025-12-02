package com.library.polargx.data.links.local

interface LinksLocalDatasource {
    suspend fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean)
    suspend fun isFirstTimeLaunch(): Boolean
}
