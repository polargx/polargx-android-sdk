package com.library.polargx.data.push.remote

import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import io.ktor.client.statement.HttpResponse

interface PushRemoteDatasource {
    suspend fun registerFCMDeviceToken(
        request: RegisterFCMRequest?
    ): HttpResponse

    suspend fun deregisterFCMDeviceToken(
        bundleID: String?,
        organizationUnid: String?,
        platform: String?,
        token: String?,
        userUnid: String?,
    ): HttpResponse
}