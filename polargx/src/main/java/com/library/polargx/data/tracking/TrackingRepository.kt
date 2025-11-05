package com.library.polargx.data.tracking

import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import io.ktor.client.statement.HttpResponse

interface TrackingRepository {
    suspend fun onAppDied()
    suspend fun onLoggedOut()
    suspend fun onTokenExpired()

    suspend fun updateUser(request: UpdateUserRequest?): HttpResponse
    suspend fun trackEvent(request: TrackEventRequest?): HttpResponse
}
