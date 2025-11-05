package com.library.polargx.data.push.remote

import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class PushRemoteDatasourceImpl(
    private val client: HttpClient,
) : PushRemoteDatasource {

    companion object {
        const val TAG = ">>>PolarPushRemoteDatasourceImpl"
    }

    override suspend fun registerFCMDeviceToken(
        request: RegisterFCMRequest?
    ): HttpResponse {
        return client.post {
            url.path("api/v1/users/device-tokens")
            setBody(request)
        }
    }

    override suspend fun deregisterFCMDeviceToken(
        bundleID: String?,
        organizationUnid: String?,
        platform: String?,
        token: String?,
        userUnid: String?
    ): HttpResponse {
        return client.delete {
            url.path("api/v1/users/device-tokens")
            parameter("bundleID", bundleID)
            parameter("organizationUnid", organizationUnid)
            parameter("platform", platform)
            parameter("token", token)
            parameter("userUnid", userUnid)
        }
    }
}