package com.library.polargx.session

import android.content.Context
import com.library.polargx.PolarConstants
import com.library.polargx.UntrackedEvent
import com.library.polargx.data.push.PushRepository
import com.library.polargx.data.push.remote.register.RegisterFCMRequest
import com.library.polargx.data.tracking.TrackingRepository
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import com.library.polargx.extension.isConnection
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.helpers.SystemInfo
import com.library.polargx.models.TrackEventModel
import com.library.polargx.models.push.RegisterPushModel
import com.library.polargx.models.UpdateUserModel
import com.library.polargx.models.push.PushToken
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
import java.io.File

/**
 * Purpose: create user if needed by calling UpdateUser api.
 * Manage events since userId to be set and send to backend.
 * One UserSession instance will be created for only one user (userID).
 */
data class UserSession(
    val organizationUnid: String?,
    val packageName: String?,
    val userID: String?,
    val trackingFileStorage: File?
) : KoinComponent {

    companion object {
        const val TAG = ">>>PolarUserSession"
    }

    private val mTrackingRepository by inject<TrackingRepository>()

    private var isValid = true

    private var attributes = mapOf<String, Any?>()
    private var attributesVersion: Int = 0
    private var attributesIsSending = false
    private var scheduledRetryUpdatingUserWorkItem: Job? = null

    private val trackingEventQueue by lazy { TrackingEventQueue(trackingFileStorage) }
    private val registerPushWorker = RegisterPushWorker()

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

    suspend fun invalidate() = withContext(Dispatchers.IO) {
        if (!isValid) return@withContext
        isValid = false

        trackingEventQueue.setReady(false)
        registerPushWorker.setReady(false)
        Logger.d(TAG, "Invalidate user session: $userID")
    }

    /**
     * Sending user attributes and user id to backend. This API call will create an user if need. After successful, we need to make `trackingEventQueue` to be ready and sending events if needed.
     * Stop sending retrying process if server returns status code #403.
     * Retry when network connection issue, server returns status code #400.
     */
    private suspend fun startToUpdateUser(
        immediate: Boolean
    ) = withContext(Dispatchers.IO) {
        //Make sure once startToUpdateUser running per user session
        if (attributesIsSending) return@withContext

        scheduledRetryUpdatingUserWorkItem?.cancel()
        attributesIsSending = true

        var waitTimeInMillis = if (immediate)
            0
        else
            PolarConstants.API.DELAY_TO_UPDATE_PROFILE_DURATION_IN_MILLIS
        var retry = false
        var submitError: Exception? = null

        do {
            try {
                //Delay for collecting enough information - prevent multiple api calls
                //Use the newest attributes at time the API calls.
                //After successful, compare sent attributes version with the newest attributes version to decide run the sending again.

                delay(timeMillis = waitTimeInMillis)

                val tempAttributesVersion = attributesVersion
                val tempAttributes = attributes
                val user = UpdateUserModel(
                    organizationUnid,
                    userID,
                    tempAttributes
                )
                val response = mTrackingRepository.updateUser(
                    request = UpdateUserRequest.from(user)
                )
                if (!response.status.isSuccess()) {
                    throw ApiError.ServerError.fromJson(response.bodyAsText())
                }
                submitError = null
                if (tempAttributesVersion != attributesVersion) {
                    waitTimeInMillis = PolarConstants.API.DELAY_TO_UPDATE_PROFILE_DURATION_IN_MILLIS
                    retry = true
                } else {
                    retry = false
                }
            } catch (ex: Exception) {
                when {
                    ex.isConnection() -> {
                        Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $ex")
                        submitError = ex
                        scheduleTaskToRetryUpdatingUser(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_CONNECTION_ERROR_IN_MILLIS) //5s
                        retry = false
                    }

                    ex is ApiError.ServerError && ex.error?.statusCode == 403 -> {
                        Logger.d(TAG, "UpdateUser: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                        submitError = ex
                        retry = false
                    }

                    else -> {
                        scheduleTaskToRetryUpdatingUser(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_SERVER_ERROR_IN_MILLIS) //5s
                        retry = false
                    }
                }
            }
        } while (retry)

        if (submitError == null) {
            registerPushWorker.setReady(true)
            registerPushWorker.startToRegisterPushToken()

            trackingEventQueue.setReady(true)
            trackingEventQueue.sendEventsIfNeeded()
        }

        //Mark startToUpdateUser is not running
        attributesIsSending = false
    }

    private fun scheduleTaskToRetryUpdatingUser(durationInMillis: Long) {
        if (scheduledRetryUpdatingUserWorkItem?.isActive == true) {
            scheduledRetryUpdatingUserWorkItem?.cancel()
        }
        scheduledRetryUpdatingUserWorkItem = CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = durationInMillis)
            startToUpdateUser(immediate = false)
        }
    }

    suspend fun setPushToken(context: Context?, pushToken: PushToken?) =
        withContext(Dispatchers.IO) {
            if (!isValid) return@withContext

            registerPushWorker.set(
                RegisterPushModel.from(
                    organizationUnid = organizationUnid,
                    userUnid = userID,
                    bundleID = packageName,
                    pushToken = pushToken,
                    data = SystemInfo.getTrackingDeviceInfo(context = context)
                )
            )
            registerPushWorker.startToRegisterPushToken()
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