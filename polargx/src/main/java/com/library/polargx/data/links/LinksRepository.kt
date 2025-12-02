package com.library.polargx.data.links

import com.library.polargx.data.links.remote.track_link.TrackLinkClickRequest
import com.library.polargx.data.links.local.update_link.UpdateLinkClickRequest
import io.ktor.client.statement.HttpResponse

interface LinksRepository {
    suspend fun onAppDied()
    suspend fun onLoggedOut()
    suspend fun onTokenExpired()

    suspend fun setFirstTimeLaunch(isFirstTimeLaunch: Boolean)
    suspend fun isFirstTimeLaunch(): Boolean

    suspend fun getLinkData(domain: String?, slug: String?): HttpResponse
    suspend fun trackLinkClick(request: TrackLinkClickRequest?): HttpResponse
    suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?): HttpResponse
    suspend fun matchLinkClick(fingerprint: String?): HttpResponse

}
