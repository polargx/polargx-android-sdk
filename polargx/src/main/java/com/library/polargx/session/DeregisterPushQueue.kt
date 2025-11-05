package com.library.polargx.session

import com.library.polargx.PolarConstants
import com.library.polargx.data.push.PushRepository
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.JsonConfig
import com.library.polargx.helpers.Logger
import com.library.polargx.extension.isConnection
import com.library.polargx.models.push.PushToken
import com.library.polargx.models.push.RegisterPushModel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Purpose: fetch Deregister events from disk and manage Deregister events.
 */
class DeregisterPushQueue(
    val packageName: String?,
    val organizationUnid: String?,
    val file: File
) : KoinComponent {

    companion object {
        const val TAG = ">>>PolarDeregisterPushQueue"
    }

    private val mPushRepository by inject<PushRepository>()
    private var pushToken: PushToken? = null

    var userUnids = mutableListOf<String?>()
        private set
    var isRunning = false
        private set

    private var scheduledRetryDeregisteringPushWorkItem: Job? = null

    /**
     * Fetch underegistered userUnids from fileUrl
     */
    init {
        try {
            val data = file.readText()
            userUnids = JsonConfig.json.decodeFromString<MutableList<String?>>(data)
        } catch (e: Exception) {
            userUnids = mutableListOf()
        }
    }

    fun isReady(): Boolean {
        return pushToken != null
    }

    suspend fun setPushToken(pushToken: PushToken) {
        this.pushToken = pushToken
        startDeregisteringPushIfNeeded()
    }

    suspend fun push(userUnid: String?) = withContext(Dispatchers.IO) {
        userUnids.add(userUnid)
        save()
    }

    private fun willPop(): String? {
        return userUnids.firstOrNull()
    }

    private suspend fun pop() = withContext(Dispatchers.IO) {
        if (userUnids.isNotEmpty()) {
            userUnids.removeFirstOrNull()
            save()
        }
    }

    private suspend fun save() = withContext(Dispatchers.IO) {
        try {
            val cacheUserUnids = userUnids.toList()
            val data = JsonConfig.json.encodeToString(cacheUserUnids)
            file.writeText(data)
        } catch (e: Exception) {
            error("??? $e")
        }
    }

    /**
     * Deregistering FCM device token, Only one progress need to be ran at the time.
     */
    suspend fun startDeregisteringPushIfNeeded() = withContext(Dispatchers.IO) {
        if (!isReady() || isRunning || pushToken == null) return@withContext

        scheduledRetryDeregisteringPushWorkItem?.cancel()
        isRunning = true

        while (true) {
            val userUnid = willPop() ?: break

            try {
                val registerPush = RegisterPushModel.from(
                    bundleID = packageName,
                    organizationUnid = organizationUnid,
                    pushToken = pushToken,
                    userUnid = userUnid,
                    data = mutableMapOf()
                )
                val response = mPushRepository.deregisterFCMDeviceToken(registerPush)
                if (!response.status.isSuccess()) {
                    throw ApiError.ServerError.fromJson(response.bodyAsText())
                }
                pop()
            } catch (ex: Exception) {
                when {
                    ex.isConnection() -> {
                        Logger.d(TAG, "Deregister: failed ⛔ + stopped ⛔: $ex")
                        try {
                            scheduleTaskToRetryDeregisteringPush(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_CONNECTION_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Deregister:scheduledRetryDeregisteringPush: failed ⛔ + next 🔁: $ex"
                            )
                        } finally {
                            break
                        }
                    }

                    else -> {
                        Logger.d(TAG, "Deregister: failed ⛔ + next 🔁: $ex")
                        try {
                            scheduleTaskToRetryDeregisteringPush(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_SERVER_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Deregister:scheduledRetryDeregisteringPush: failed ⛔ + next 🔁: $ex"
                            )
                        } finally {
                            break
                        }
                    }
                }
            }
        }

        isRunning = false
    }

    /**
     * Schedule to retry sending events with specified time.
     * If call `sendEventsIfNeeded` during the wait time, `sendEventsIfNeeded` will be continue and cancel this scheduling.
     */
    private fun scheduleTaskToRetryDeregisteringPush(durationInMillis: Long) {
        if (scheduledRetryDeregisteringPushWorkItem?.isActive == true) {
            scheduledRetryDeregisteringPushWorkItem?.cancel()
        }
        scheduledRetryDeregisteringPushWorkItem = CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = durationInMillis)
            startDeregisteringPushIfNeeded()
        }
    }
}