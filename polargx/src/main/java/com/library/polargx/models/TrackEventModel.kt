package com.library.polargx.models

import DictionaryModel
import androidx.annotation.StringDef
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class TrackEventModel(
    @SerialName("clobberMatchingAttributes")
    val clobberMatchingAttributes: Boolean?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("eventName")
    val eventName: String?,
    @SerialName("eventTime")
    val eventTime: String?,
    @SerialName("eventUnid")
    val eventUnid: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    constructor(
        organizationUnid: String?,
        userID: String?,
        eventName: String?,
        eventTime: String?,
        data: Map<String, Any?>?
    ) : this(
        clobberMatchingAttributes = false,
        organizationUnid = organizationUnid,
        userID = userID,
        eventName = eventName,
        eventTime = eventTime,
        eventUnid = null,
        data = DictionaryModel(data)
    )
}