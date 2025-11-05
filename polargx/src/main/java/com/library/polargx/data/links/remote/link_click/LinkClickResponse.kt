package com.library.polargx.data.links.remote.link_click

import com.library.polargx.models.LinkClickModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class LinkClickResponse(
    @SerialName("data")
    val data: Data?
) {

    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClickModel?
    )
}