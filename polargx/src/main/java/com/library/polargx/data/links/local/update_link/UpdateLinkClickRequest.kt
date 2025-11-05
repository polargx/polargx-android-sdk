package com.library.polargx.data.links.local.update_link

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class UpdateLinkClickRequest(
    @SerialName("SdkUsed")
    val sdkUsed: Boolean?
)