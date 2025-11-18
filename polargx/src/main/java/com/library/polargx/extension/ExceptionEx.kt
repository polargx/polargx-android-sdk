package com.library.polargx.extension

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

fun Exception?.isConnection(): Boolean {
    if (this is UnknownHostException) return true
    if (this is SocketTimeoutException) return true
    if (this is ConnectException) return true
    if (this is SSLHandshakeException) return true
    return false
}