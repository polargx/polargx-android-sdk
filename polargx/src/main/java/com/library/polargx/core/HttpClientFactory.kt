package com.library.polargx.core

import android.content.Context
import android.content.Intent
import android.util.Log
import com.library.polargx.Configuration
import com.library.polargx.PolarConstants
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.JsonConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.append
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.library.polargx.helpers.Logger
import com.library.polargx.helpers.ThLocker
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay

object HttpClientFactory {

    private const val TAG = ">>>PolarHttpClient"


    /**
     * Ktor plugin that handles HTTP 429 (Too Many Requests) rate limiting globally.
     *
     * How it works:
     * 1. Before each request, waits if locker is locked
     * 2. After each response, checks for 429 status
     * 3. If 429: locks globally, waits, unlocks, retries automatically
     * 4. All other concurrent requests wait during the lock period
     *
     * Equivalent to iOS APIService rate limit handling.
     */

    private fun getRateLimitPlugin(context: Context, locker: ThLocker): ClientPlugin<Unit> {
        return createClientPlugin("RateLimitPlugin") {
            // Intercept before sending request
            onRequest { request, _ ->
                Logger.d(
                    TAG,
                    "RateLimitPlugin:onRequest: locker=$locker, locker.isLocked=${locker.isLocked()} request=${request.url}, request.body=${request.body}"
                )
                // Wait if another request is handling rate limit
                locker.waitForUnlock()
            }

            // Intercept after receiving response
//            var isSent = false
            onResponse { response ->
                Logger.d(
                    TAG,
                    "RateLimitPlugin:onResponse: locker=$locker, isLocked=${locker.isLocked()},response=$response"
                )
                if (response.status == HttpStatusCode.TooManyRequests) {
//                if (!isSent) {
                    // Lock all requests
                    locker.lock()

                    // Wait before allowing retries
                    Logger.d(
                        TAG,
                        "RateLimitPlugin:onResponse:TooManyRequests: locker=$locker, locker=${locker.isLocked()}, start wait..."
                    )
//                    val intent = Intent(PolarConstants.RATE_LIMIT_WAITED_ACTION)
//                    intent.setPackage(context.packageName)
//                    context.sendBroadcast(intent)
//                    isSent=true
                    delay(PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_TIME_LIMITS_IN_MILLIS)
                    Logger.d(TAG, "RateLimitPlugin:onResponse:TooManyRequests: end wait")

                    // Unlock - all waiting requests will now proceed
                    locker.unlock()
                }
            }
        }
    }

    fun createRateLimitHttpClient(
        context: Context,
        xApiKey: String,
        locker: ThLocker,
    ): HttpClient {
        val client = HttpClient(Android) {
            engine {
                Logger.d(TAG, "install: engine")
                socketTimeout = 60_000
                connectTimeout = 60_000
            }
            defaultRequest {
                Logger.d(TAG, "install: defaultRequest")
                url {
                    Logger.d(TAG, "install: defaultRequest:url")
                    protocol = URLProtocol.HTTPS
                    host = Configuration.Env.server
                }
                headers {
                    Logger.d(TAG, "install: defaultRequest:headers")
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append("x-api-key", xApiKey)
                }
            }

            // Install rate limit plugin FIRST to handle 429 globally
            install(
                getRateLimitPlugin(
                    context = context,
                    locker = locker
                )
            ) {
                Logger.d(TAG, "install: RateLimitPlugin")
            }

            install(HttpRequestRetry) {
                Logger.d(TAG, "install: HttpRequestRetry")
                retryOnServerErrors(0)
                exponentialDelay()

                // Retry on 429 (Too Many Requests) - the RateLimitPlugin will handle the lock/delay
//                retryIf(maxRetries = 3) { _, response ->
//                    Logger.d(TAG, "install: HttpRequestRetry:retryIf:response=$response")
//                    response.status.value == 429
//                }
            }
            install(Logging) {
                Logger.d(TAG, "install: Logging")
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.d(TAG, message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                Logger.d(TAG, "install: ContentNegotiation")
                json(json = JsonConfig.json)
            }
            install(HttpCache) {
                Logger.d(TAG, "install: HttpCache")
            }
            install(HttpTimeout) {
                Logger.d(TAG, "install: HttpTimeout")
                requestTimeoutMillis = 60_000
            }

            HttpResponseValidator {
                Logger.d(TAG, "HttpResponseValidator:")
                validateResponse { response ->
                    Logger.d(
                        TAG,
                        "HttpResponseValidator:validateResponse: response=$response"
                    )
                    // Don't throw on 429 - let RateLimitPlugin and HttpRequestRetry handle it
                    if (!response.status.isSuccess() && response.status.value != 429) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    Logger.d(
                        TAG,
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError.ServerError.fromJson(errorData)
                        Logger.d(
                            TAG,
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                        throw ApiError.ServerError.fromJson(errorData)
                    } catch (ex: Throwable) {
                        Logger.d(
                            TAG,
                            "response: ex=${ex}"
                        )
                        throw ex
                    }
                }
            }

        }
        return client
    }

    fun createDefaultClient(
        host: String?,
        requestTimeoutMillis: Long? = null,
        socketTimeout: Int? = null,
        connectTimeout: Int? = null,
        headers: Map<String?, String?>? = null,
        responseValidator: ((HttpResponse) -> Unit?)? = null,
        handleResponseExceptionWithRequest: (suspend (Throwable, HttpRequest) -> Unit?)? = null,
    ): HttpClient {
        return HttpClient(Android) {
            engine {
                this.socketTimeout = socketTimeout ?: 60_000
                this.connectTimeout = connectTimeout ?: 60_000
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    this.host = host ?: ""
                }
                headers {
                    headers?.forEach { entry ->
                        header(entry.key ?: "", entry.value ?: "")
                    }
                }
            }

//            install(Auth) {
//                bearer {
//                    loadTokens {
//                        BearerTokens("eyJraWQiOiJmR3J6alFIdjZ1ZkY5VkpjeWRIYUdONzh6akVhTmtlZlVNYlV2NEhUTzlRPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJlNGE4MTRlOC0xMDQxLTcwMDYtZDAwMy0xZDdlYzQ2ZDM4MzQiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9QWU5ZQWxlVGsiLCJjbGllbnRfaWQiOiI2dm0wbzRuZ2ZrMmo1czczYjhnbjBmaDZhYiIsIm9yaWdpbl9qdGkiOiIwYWUzZjQwMC00YzlhLTQwNzMtOGYzMy05NDBmYmQxM2Q2ODAiLCJldmVudF9pZCI6IjQ1ZDhkZjc0LTYzODUtNDk5YS05Njc0LWEyZjBjNTQyY2MxZCIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE3NTM0NDI0NjUsImV4cCI6MTc1MzQ0NjA2NSwiaWF0IjoxNzUzNDQyNDY1LCJqdGkiOiI2YmYyN2RiNy04Y2YyLTQxNjctYTQyMS0yNDdlNTlhNjM4ZTQiLCJ1c2VybmFtZSI6ImU0YTgxNGU4LTEwNDEtNzAwNi1kMDAzLTFkN2VjNDZkMzgzNCJ9.4Gltjf5HShE1L6Y6JTjI2qu6_tb1J0Jxg45ZAxsyRNBEgaVPHT0KKLj0mifXkKvWcroia41D9qhIzKXtYcxL3HXdCrBemALz4NQMJlC3HljJliZ8rhCw7Uoi0Hk6g1tupXNnCmB7FHGJJIqm3aAsLspjFsEPeZdqAq0__Bf5tp2pljFB5m4MkrJ31giNXua2FylwrAW2-g_Hv1NaLHXr_j45E1niZWL3g8De6_31q-r7aTqretfPJiomwe2oWDF2mFrCfihWCxtO_sa4f9ZxoWlpsxuAInv_KJpK_5zq9BMYSknEGESz-paosBj95JbDnjMZTbfyzxfMCb4Eg5s_aw", "")
//                    }
//                }
//            }

            install(HttpRequestRetry) {
                retryOnServerErrors(0)
                exponentialDelay()
            }
            install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Log.i(TAG, message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
//                        prettyPrint = true
                    }
                )
            }
            install(HttpCache)
            install(HttpTimeout) {
                this.requestTimeoutMillis = requestTimeoutMillis ?: 60_000
            }

            HttpResponseValidator {
                validateResponse { response ->
                    Log.d(TAG, "validateResponse: response=$response")
                    responseValidator?.let {
                        responseValidator(response)
                        return@validateResponse
                    }
                    if (!response.status.isSuccess()) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    Log.d(
                        TAG,
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    handleResponseExceptionWithRequest?.let {
                        handleResponseExceptionWithRequest(cause, request)
                        return@handleResponseExceptionWithRequest
                    }
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError.ServerError.fromJson(errorData)
                        Log.d(
                            TAG,
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                        when (error.error?.statusCode) {

                        }
                        throw ApiError.ServerError.fromJson(errorData)
                    } catch (ex: Throwable) {
                        Log.d(TAG, "response: ex=${ex}")
                        throw ex
                    }
                }
            }
        }
    }
}