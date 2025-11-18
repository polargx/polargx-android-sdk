package com.library.polargx.data.links.remote

import com.library.polargx.data.links.remote.track_link.TrackLinkClickRequest
import com.library.polargx.data.links.local.update_link.UpdateLinkClickRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class LinksRemoteDatasourceImpl(
    private val client: HttpClient,
) : LinksRemoteDatasource {

    companion object {
        const val TAG = ">>>PolarLinksRemoteDatasourceImpl"
    }

    override suspend fun getLinkData(
        domain: String?,
        slug: String?
    ): HttpResponse {
        return client.get {
            url.path("api/v1/links/resolve")
            parameter("domain", domain)
            parameter("slug", slug)
        }
    }

    override suspend fun trackLinkClick(request: TrackLinkClickRequest?): HttpResponse {
        return client.post {
            url.path("api/v1/links/clicks")
            setBody(request)
        }
    }

    override suspend fun updateLinkClick(
        clickUnid: String?,
        request: UpdateLinkClickRequest?
    ): HttpResponse {
        return client.put {
            url.path("api/v1/links/clicks/$clickUnid")
            setBody(request)
        }
    }

    override suspend fun matchLinkClick(fingerprint: String?): HttpResponse {
        return client.get {
            url.path("api/v1/links/clicks/match")
            parameter("fingerprint", fingerprint)
        }
    }
}