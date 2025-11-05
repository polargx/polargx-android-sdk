package com.library.polargx.models.push

import DictionaryModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class RegisterPushModel(
    @SerialName("bundleID")
    val bundleID: String?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("platform")
    val platform: String?,
    @SerialName("token")
    val token: String?,
    @SerialName("userUnid")
    val userUnid: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {
    companion object {
        fun from(
            organizationUnid: String?,
            userUnid: String?,
            bundleID: String?,
            pushToken: PushToken?,
            data: Map<String, Any?>?
        ): RegisterPushModel {
            var platform: String? = null
            var token: String? = null
            when (pushToken) {
                is PushToken.GCM -> {
                    platform = "GCM"
                    token = pushToken.token
                }

                else -> null
            }
            return RegisterPushModel(
                organizationUnid = organizationUnid,
                userUnid = userUnid,
                bundleID = bundleID,
                token = token,
                platform = platform,
                data = DictionaryModel(data)
            )
        }
    }
}