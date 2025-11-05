package com.library.polargx.data.tracking.remote.update_user

import DictionaryModel
import com.library.polargx.models.UpdateUserModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class UpdateUserRequest(
    @SerialName("clobberMatchingAttributes")
    val clobberMatchingAttributes: Boolean?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    companion object {
        fun from(user: UpdateUserModel?): UpdateUserRequest? {
            if (user == null) return null
            return UpdateUserRequest(
                clobberMatchingAttributes = user.clobberMatchingAttributes,
                organizationUnid = user.organizationUnid,
                userID = user.userID,
                data = user.data
            )
        }
    }
}