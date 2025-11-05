package com.library.polargx.data.push.remote.register

import DictionaryModel
import com.library.polargx.models.push.RegisterPushModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class RegisterFCMRequest(
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
        fun from(fcm: RegisterPushModel?): RegisterFCMRequest? {
            if (fcm == null) return null
            return RegisterFCMRequest(
                bundleID = fcm.bundleID,
                organizationUnid = fcm.organizationUnid,
                platform = fcm.platform,
                token = fcm.token,
                userUnid = fcm.userUnid,
                data = fcm.data
            )
        }
    }
}