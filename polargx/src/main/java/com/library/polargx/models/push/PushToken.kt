package com.library.polargx.models.push

sealed class PushToken() {
    data class GCM(val token: String) : PushToken()
}
