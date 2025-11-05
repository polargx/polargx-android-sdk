package com.library.polargx.helpers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//@OptIn(InternalSerializationApi::class)
//@Parcelize
//@Serializable
//data class ApiError(
//    @SerialName("code")
//    val code: Int? = null,
//
//    @SerialName("message")
//    override val message: String? = null,
//
//    @SerialName("api_version")
//    val apiVersion: Double? = null,
//) : Exception(), Parcelable {
//
//    companion object {
//        fun fromJson(data: String?): ApiError {
//            if (data == null) return ApiError()
//            return Json.decodeFromString<ApiError>(data)
//        }
//    }
//}

open class ApiError : Exception() {
    data class ValidationError(override val message: String) : ApiError()
    data class NetworkError(override val message: String) : ApiError()

    @OptIn(InternalSerializationApi::class)
    @Serializable
    open class ServerError(
        @SerialName("error")
        val error: Error? = null,
        @SerialName("requestId")
        val requestId: String? = null,
        @SerialName("version")
        val version: String? = null,
    ) : ApiError() {
        override val message = error?.message

        @Serializable
        data class Error(
            @SerialName("code")
            val code: String? = null,
            @SerialName("message")
            val message: String? = null,
            @SerialName("statusCode")
            val statusCode: Int? = null,
            @SerialName("details")
            val details: Details? = null,
        ) {
            @Serializable
            data class Details(
                @SerialName("errors")
                val errors: List<Error>? = null,
            ) {
                @Serializable
                data class Error(
                    @SerialName("code")
                    val code: String? = null,
                    @SerialName("message")
                    val message: String? = null,
                )
            }
        }

        companion object {
            fun fromJson(data: String?): ServerError {
                if (data == null) return ServerError()
                return JsonConfig.json.decodeFromString<ServerError>(data)
            }
        }
    }

    data class TokenExpiredError(override val message: String?) : ApiError()
    data class TokenRequiredError(override val message: String?) : ApiError()
}