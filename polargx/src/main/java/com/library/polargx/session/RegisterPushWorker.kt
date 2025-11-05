package com.library.polargx.session

import com.library.polargx.PolarConstants
import com.library.polargx.data.push.PushRepository
import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.extension.isConnection
import com.library.polargx.models.push.RegisterPushModel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RegisterPushWorker() : KoinComponent {

    companion object Companion {
        const val TAG = ">>>PolarRegisterPushQueue"
    }

    private val mPushRepository by inject<PushRepository>()

    private var pendingRegisterPushToken: RegisterPushModel? = null
    var isReady = false
        private set
    var isRunning = false
        private set

    private var scheduledRetryRegisteringPushTokenWorkItem: Job? = null

    suspend fun setReady(newReady: Boolean) = withContext(Dispatchers.IO) {
        isReady = newReady

        if (!newReady) {
            scheduledRetryRegisteringPushTokenWorkItem?.cancel()
        }
    }

    /**
     * Event still pushed to the queue if queue is not ready.
     */
    suspend fun set(push: RegisterPushModel) = withContext(Dispatchers.IO) {
        pendingRegisterPushToken = push
    }

    suspend fun startToRegisterPushToken() = withContext(Dispatchers.IO) {
        if (!isReady || isRunning || pendingRegisterPushToken == null) return@withContext

        scheduledRetryRegisteringPushTokenWorkItem?.cancel()
        isRunning = true

        var retry = false
        do {
            try {
                val registeringPushToken = pendingRegisterPushToken
                val request = RegisterFCMRequest.from(registeringPushToken)
                val response = mPushRepository.registerFCMDeviceToken(request)
                if (!response.status.isSuccess()) {
                    throw ApiError.ServerError.fromJson(response.bodyAsText())
                }
                if (registeringPushToken?.platform == pendingRegisterPushToken?.platform
                    && registeringPushToken?.token == pendingRegisterPushToken?.token
                ) {
                    pendingRegisterPushToken = null
                }
                retry = pendingRegisterPushToken != null
            } catch (ex: Exception) {
                when {
                    ex.isConnection() -> {
                        Logger.d(TAG, "Register: failed ⛔ + stopped ⛔: $ex")
                        try {
                            scheduleTaskToRetryRegisterPushToken(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_CONNECTION_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Register:scheduledRetryRegisteringPush: failed ⛔ + next 🔁: $ex"
                            )
                        } finally {
                            retry = false
                        }
                    }

                    else -> {
                        Logger.d(TAG, "Register: failed ⛔ + next 🔁: $ex")
                        try {
                            scheduleTaskToRetryRegisterPushToken(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_SERVER_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Register:scheduledRetryRegisteringPush: failed ⛔ + next 🔁: $ex"
                            )
                        } finally {
                            retry = false
                        }
                    }
                }
            }
        } while (retry)

        isRunning = false
    }

    private fun scheduleTaskToRetryRegisterPushToken(durationInMillis: Long) {
        if (scheduledRetryRegisteringPushTokenWorkItem?.isActive == true) {
            scheduledRetryRegisteringPushTokenWorkItem?.cancel()
        }
        scheduledRetryRegisteringPushTokenWorkItem = CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = durationInMillis)
            startToRegisterPushToken()
        }
    }
}