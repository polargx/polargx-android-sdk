package com.library.polargx.data.others.remote

import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import io.ktor.client.statement.HttpResponse

interface OthersRemoteDatasource {
    suspend fun updateUser(request: UpdateUserRequest?): HttpResponse
    suspend fun trackEvent(request: TrackEventRequest?): HttpResponse
}