package com.library.polargx.data.links

import com.library.polargx.data.links.remote.track_link.TrackLinkClickRequest
import com.library.polargx.data.links.local.update_link.UpdateLinkClickRequest
import com.library.polargx.data.links.local.LinksLocalDatasource
import com.library.polargx.data.links.remote.LinksRemoteDatasource
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LinksRepositoryImpl(
    private val localDatasource: LinksLocalDatasource,
    private val remoteDatasource: LinksRemoteDatasource
) : LinksRepository {

    companion object {
        const val TAG = ">>>PolarLinksRepositoryImpl"
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

    override suspend fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean) {
        localDatasource.setFirstTimeLaunch(isFirstTimeLaunch)
    }

    override suspend fun isFirstTimeLaunch(): Boolean {
       return localDatasource.isFirstTimeLaunch()
    }

    override suspend fun getLinkData(
        domain: String?,
        slug: String?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.getLinkData(
            domain = domain,
            slug = slug
        )
    }

    override suspend fun trackLinkClick(
        request: TrackLinkClickRequest?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.trackLinkClick(request = request)
    }

    override suspend fun updateLinkClick(
        clickUnid: String?,
        request: UpdateLinkClickRequest?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.updateLinkClick(
            clickUnid = clickUnid,
            request = request
        )
    }

    override suspend fun matchLinkClick(
        fingerprint: String?
    ): HttpResponse = withContext(Dispatchers.IO) {
        return@withContext remoteDatasource.matchLinkClick(
            fingerprint = fingerprint,
        )
    }
}
