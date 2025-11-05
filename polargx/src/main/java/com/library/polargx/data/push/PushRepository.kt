package com.library.polargx.data.push

import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import com.library.polargx.models.push.RegisterPushModel
import io.ktor.client.statement.HttpResponse

interface PushRepository {
    suspend fun onAppDied()
    suspend fun onLoggedOut()
    suspend fun onTokenExpired()

    suspend fun registerFCMDeviceToken(request: RegisterFCMRequest?): HttpResponse
    suspend fun deregisterFCMDeviceToken(
        registerPush:RegisterPushModel?
    ): HttpResponse
}
