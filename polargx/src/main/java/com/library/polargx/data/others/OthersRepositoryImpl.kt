package com.library.polargx.data.others

import android.content.Context
import com.library.polargx.data.others.local.OthersLocalDatasource
import com.library.polargx.data.others.remote.OthersRemoteDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OthersRepositoryImpl(
    private val localDatasource: OthersLocalDatasource,
    private val remoteDatasource: OthersRemoteDatasource
) : OthersRepository {

    companion object {
        const val TAG = ">>>PolarOthersRepositoryImpl"
    }

    override suspend fun onAppDied() {
    }

    private suspend fun onUnauthenticated() {
    }

    override suspend fun onLoggedOut() {
        onUnauthenticated()
    }

    override suspend fun onTokenExpired() {
        onUnauthenticated()
    }

    override suspend fun isFirstTimeLaunch(
        context: Context?,
        nowInMillis: Long
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext localDatasource.isFirstTimeLaunch(
            context = context,
            nowInMillis = nowInMillis
        )
    }
}
