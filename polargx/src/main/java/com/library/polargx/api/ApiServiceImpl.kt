package com.library.polargx.api

import com.library.polargx.data.links.remote.link_click.LinkClickResponse
import com.library.polargx.data.links.remote.link_data.LinkDataResponse
import com.library.polargx.data.links.remote.track_link.TrackLinkClickRequest
import com.library.polargx.data.links.remote.track_link.TrackLinkClickResponse
import com.library.polargx.data.links.local.update_link.UpdateLinkClickRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.models.LinkClickModel
import com.library.polargx.models.LinkDataModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.path

class ApiServiceImpl(
    private val client: HttpClient,
) : ApiService {

    override suspend fun getLinkData(domain: String?, slug: String?): LinkDataModel? {
        val response = client.get {
            url.path("api/v1/links/resolve")
            parameter("domain", domain)
            parameter("slug", slug)
        }
        if (response.status.isSuccess()) {
            val body = response.body<LinkDataResponse>()
            return body.data?.sdkLinkData
        }
        throw ApiError.ServerError.fromJson(response.bodyAsText())
    }

    override suspend fun trackLinkClick(request: TrackLinkClickRequest?): LinkClickModel? {
        val response = client.post {
            url.path("api/v1/links/clicks")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            val body = response.body<TrackLinkClickResponse?>()
            return body?.data?.linkClick
        }
        throw ApiError.ServerError.fromJson(response.bodyAsText())
    }

    override suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?) {
        val response = client.put {
            url.path("api/v1/links/clicks/$clickUnid")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError.ServerError.fromJson(response.bodyAsText())
    }

    override suspend fun matchLinkClick(fingerprint: String?): LinkClickModel? {
        val response = client.get {
            url.path("api/v1/links/clicks/match")
            parameter("fingerprint", fingerprint)
        }
        if (response.status.isSuccess()) {
            val body = response.body<LinkClickResponse?>()
            return body?.data?.linkClick
        }
        throw ApiError.ServerError.fromJson(response.bodyAsText())
    }
}