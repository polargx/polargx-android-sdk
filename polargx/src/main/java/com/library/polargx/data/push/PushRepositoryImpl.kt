package com.library.polargx.data.push

import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import com.library.polargx.data.push.local.PushLocalDatasource
import com.library.polargx.data.push.remote.PushRemoteDatasource
import com.library.polargx.models.push.RegisterPushModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PushRepositoryImpl(
    private val localDatasource: PushLocalDatasource,
    private val remoteDatasource: PushRemoteDatasource
) : PushRepository {

    companion object {
        const val TAG = ">>>PolarPushRepositoryImpl"
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

    override suspend fun registerFCMDeviceToken(
        request: RegisterFCMRequest?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.registerFCMDeviceToken(
            request = request,
        )
    }

    override suspend fun deregisterFCMDeviceToken(
        registerPush: RegisterPushModel?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.deregisterFCMDeviceToken(
            bundleID = registerPush?.bundleID,
            organizationUnid = registerPush?.organizationUnid,
            platform = registerPush?.platform,
            token = registerPush?.token,
            userUnid = registerPush?.userUnid
        )
    }
}
