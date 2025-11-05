package com.library.polargx.data.links.remote.track_link

import android.os.Parcelable
import com.library.polargx.models.LinkClickModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Parcelize
@Serializable
class TrackLinkClickResponse(
    @SerialName("data")
    val data: Data?
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClickModel?
    ) : Parcelable
}