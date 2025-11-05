package com.library.polargx.data.tracking.remote

import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class TrackingRemoteDatasourceImpl(
    private val client: HttpClient,
) : TrackingRemoteDatasource {

    companion object {
        const val TAG = ">>>PolarLinksRemoteDatasourceImpl"
    }

    override suspend fun updateUser(request: UpdateUserRequest?): HttpResponse {
        return client.post {
            url.path("api/v1/users/profile")
            setBody(request)
        }
    }

    override suspend fun trackEvent(request: TrackEventRequest?): HttpResponse {
        return client.post {
            url.path("api/v1/events")
            setBody(request)
        }
    }
}