package com.library.polargx

import com.library.polargx.api.ApiService
import com.library.polargx.api.fcm_tokens.deregister.DeregisterFCMRequest
import com.library.polargx.api.fcm_tokens.register.RegisterFCMRequest
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.models.DeregisterFCMModel
import com.library.polargx.models.TrackEventModel
import com.library.polargx.models.RegisterFCMModel
import com.library.polargx.models.UpdateUserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Purpose: create user if needed by calling UpdateUser api.
 * Manage events since userId to be set and send to backend.
 * One UserSession instance will be created for only one user (userID).
 */
data class UserSession(
    val organizationUnid: String,
    val userID: String,
    val trackingFileStorage: File
) : KoinComponent {

    private val apiService by inject<ApiService>()

    private var isValid = true

    private var attributes = mapOf<String, Any?>()
    private var attributesVersion: Int = 0
    private var attributesIsSending = false

    private var pendingRegisterPushToken: String? = null
    private var lastRegisteredFCMToken: String? = null

    private val trackingEventQueue by lazy { TrackingEventQueue(trackingFileStorage) }

    companion object {
        const val TAG = ">>>Polar"
    }

    /**
     * Keep all user attributes for next sending. I don't make sure server supports to merging existing user attributes and the new attributes.
     */
    suspend fun setAttributes(attrs: Map<String, Any?>) = withContext(Dispatchers.IO) {
        if (!isValid) return@withContext
        val immediateSend = attributesVersion == 0
        attributes += attrs
        attributesVersion += 1
        startToUpdateUser(immediate = immediateSend)
    }

    suspend fun setPushToken(fcm: String?) = withContext(Dispatchers.IO) {
        if (!isValid) return@withContext
        pendingRegisterPushToken = fcm
        startToRegisterPushToken()
    }

    suspend fun invalidate() = withContext(Dispatchers.IO) {
        if (!isValid) return@withContext
        isValid = false

        Logger.d(">>>Polar", "Invalidate user session: $userID")

        startToDeregisterPushToken()
    }

    /**
     * Sending user attributes and user id to backend. This API call will create an user if need. After successful, we need to make `trackingEventQueue` to be ready and sending events if needed.
     * Stop sending retrying process if server returns status code #403.
     * Retry when network connection issue, server returns status code #400.
     */
    private suspend fun startToUpdateUser(
        immediate: Boolean
    ) = withContext(Dispatchers.IO) {
        if (attributesIsSending) return@withContext
        attributesIsSending = true
        var immediate = immediate
        var retry = false
        var submitError: Exception? = null

        do {
            try {
                //Delay for collecting enough information - prevent multiple api calls
                //Use the newest attributes at time the API calls.
                //After successful, compare sent attributes version with the newest attributes version to decide run the sending again.

                if (submitError != null) {
                    delay(1000)
                } else if (!immediate) {
                    delay(PolarApp.minimumIntervalForSendingUserAttributesInMillis)
                }
                val tempAttributesVersion = attributesVersion
                val tempAttributes = attributes
                val user = UpdateUserModel(
                    organizationUnid,
                    userID,
                    tempAttributes
                )
                apiService.updateUser(request = UpdateUserRequest.from(user))
                submitError = null
                if (tempAttributesVersion != attributesVersion) {
                    immediate = false
                    retry = true
                } else {
                    retry = false
                }
            } catch (ex: Exception) {
                if (ex is ApiError && ex.code == 403) {
                    Logger.d(TAG, "UpdateUser: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = ex
                    retry = false
                } else {
                    Logger.d(TAG, "UpdateUser: failed ⛔️ + retrying 🔁: $ex")
                    submitError = ex
                    retry = true
                }
            }
        } while (retry)

        if (submitError == null) {
            trackingEventQueue.setReady()
            trackingEventQueue.sendEventsIfNeeded()
        }

        //Mark startToUpdateUser is not running
        attributesIsSending = false
    }

    /**
     * Stop sending retrying process if server returns status code #403
     * Retry when network connection issue, server returns status code #400
     */
    private suspend fun startToRegisterPushToken(
    ) = withContext(Dispatchers.IO) {
        var submitError: Exception? = null

        do {
            try {
                val registeringPushToken = pendingRegisterPushToken
                if (registeringPushToken != null) {
                    val fcm = RegisterFCMModel(organizationUnid, userID, registeringPushToken)
                    val request = RegisterFCMRequest.from(fcm)
                    apiService.registerFCM(request)
                } else {
                    lastRegisteredFCMToken = null
                }

                if (registeringPushToken == pendingRegisterPushToken) {
                    pendingRegisterPushToken = null
                }

                submitError = null
            } catch (e: Exception) {
                if (e is ApiError && e.code == 403) {
                    Logger.d(TAG, "RegisterPushToken: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = null
                } else {
                    Logger.d(TAG, "RegisterPushToken: failed ⛔️ + retrying 🔁: $e")
                    delay(1000)
                    submitError = e
                }
            }
        } while (submitError != null)

        if (!isValid) {
            startToDeregisterPushToken()
        }
    }

    private suspend fun startToDeregisterPushToken(
    ) = withContext(Dispatchers.IO) {
        var submitError: Exception? = null

        do {
            try {
                lastRegisteredFCMToken?.let { fcmToken ->
                    val fcm = DeregisterFCMModel(organizationUnid, userID, fcmToken)
                    val request = DeregisterFCMRequest.from(fcm)
                    apiService.deregisterFCM(request)
                    lastRegisteredFCMToken = null
                }
            } catch (e: Exception) {
                if (e is ApiError && e.code == 403) {
                    Logger.d(TAG, "DeregisterPushToken: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = null
                } else {
                    Logger.d(TAG, "DeregisterPushToken: failed ⛔️ + retrying 🔁: $e")
                    delay(1000)
                    submitError = e
                }
            }
        } while (submitError != null)
    }

    /**
     * Track event for user.
     */
    suspend fun trackEvents(
        events: List<UntrackedEvent>
    ) = withContext(Dispatchers.IO) {
        events.map { event ->
            val (name, date, attributes) = event
            trackingEventQueue.push(
                TrackEventModel(
                    organizationUnid = organizationUnid,
                    userID = userID,
                    eventName = name,
                    eventTime = date,
                    data = attributes
                )
            )
        }
        trackingEventQueue.sendEventsIfNeeded()
    }
}