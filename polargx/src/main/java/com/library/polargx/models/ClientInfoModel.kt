package com.library.polargx.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class ClientInfoModel(
    @SerialName("ip")
    val ip: String?,
    @SerialName("userAgent")
    val userAgent: String?,
    @SerialName("timestamp")
    val timestamp: String?
)