package com.library.polargx.data.links.remote.link_data

import com.library.polargx.models.LinkDataModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class LinkDataResponse(
    @SerialName("data")
    val data: Data?
) {

    @Serializable
    data class Data(
        @SerialName("sdkLinkData")
        val sdkLinkData: LinkDataModel?
    )
}