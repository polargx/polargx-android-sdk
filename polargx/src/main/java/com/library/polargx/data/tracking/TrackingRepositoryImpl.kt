package com.library.polargx.data.tracking

import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import com.library.polargx.data.tracking.local.TrackingLocalDatasource
import com.library.polargx.data.tracking.remote.TrackingRemoteDatasource
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackingRepositoryImpl(
    private val localDatasource: TrackingLocalDatasource,
    private val remoteDatasource: TrackingRemoteDatasource
) : TrackingRepository {

    companion object {
        const val TAG = ">>>PolarTrackingRepositoryImpl"
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

    override suspend fun updateUser(
        request: UpdateUserRequest?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.updateUser(
            request = request,
        )
    }

    override suspend fun trackEvent(
        request: TrackEventRequest?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.trackEvent(
            request = request,
        )
    }
}
