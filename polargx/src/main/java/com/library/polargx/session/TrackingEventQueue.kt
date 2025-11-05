package com.library.polargx.session

import com.library.polargx.PolarConstants
import com.library.polargx.data.tracking.TrackingRepository
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.JsonConfig
import com.library.polargx.helpers.Logger
import com.library.polargx.models.TrackEventModel
import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.extension.isConnection
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
 * Purpose: fetch events from disk and manage events.
 */
class TrackingEventQueue(val file: File?) : KoinComponent {

    companion object {
        const val TAG = ">>>PolarTrackingEventQueue"
    }

    private val mTrackingRepository by inject<TrackingRepository>()

    private var scheduledRetrySendingEventsWorkItem: Job? = null

    var events = mutableListOf<TrackEventModel>()
        private set
    var isReady = false
        private set
    var isRunning = false
        private set

    /**
     * Fetch unsent events from file.
     */
    init {
        try {
            val data = file?.readText()
            data?.let {
                events = JsonConfig.json.decodeFromString<MutableList<TrackEventModel>>(data)
            }
        } catch (e: Exception) {
            events = mutableListOf()
        }
    }

    /**
     * Set isReady flag.
     * If isReady sets to True, Events will be saved to disk, The queue is ready to send data to backend.
     * If isReady sets to False, Events is not saved to the disk.
     */
    suspend fun setReady(newReady: Boolean) = withContext(Dispatchers.IO) {
        val wasReady = isReady
        isReady = newReady

        if (!wasReady) {
            save()
        }
    }

    /**
     * Event still pushed to the queue if queue is not ready.
     */
    suspend fun push(event: TrackEventModel) = withContext(Dispatchers.IO) {
        events.add(event)
        save()
    }

    private fun willPop(): TrackEventModel? {
        return events.firstOrNull()
    }

    private suspend fun pop() = withContext(Dispatchers.IO) {
        if (events.isNotEmpty()) {
            events.removeAt(0)
            save()
        }
    }

    private suspend fun save() = withContext(Dispatchers.IO) {
        if (!isReady) return@withContext

        try {
            val cacheEvents = events.toList()
            val data = JsonConfig.json.encodeToString(cacheEvents)
            file?.writeText(data)
        } catch (e: Exception) {
            error("??? $e")
        }
    }

    private fun scheduleTaskToRetryUpdatingUserEvents(durationInMillis: Long) {
        if (scheduledRetrySendingEventsWorkItem?.isActive == true) {
            scheduledRetrySendingEventsWorkItem?.cancel()
        }
        scheduledRetrySendingEventsWorkItem = CoroutineScope(Dispatchers.IO).launch {
            delay(timeMillis = durationInMillis)
            sendEventsIfNeeded()
        }
    }

    /**
     * Sending Event progress, Only one progress need to be ran at the time.
     */
    suspend fun sendEventsIfNeeded() = withContext(Dispatchers.IO) {
        if (!isReady || isRunning) return@withContext

        scheduledRetrySendingEventsWorkItem?.cancel()
        isRunning = true

        while (true) {
            val event = willPop() ?: break

            try {
                val request = TrackEventRequest.from(event)
                val response = mTrackingRepository.trackEvent(request)
                if (!response.status.isSuccess()) {
                    throw ApiError.ServerError.fromJson(response.bodyAsText())
                }
                pop()
            } catch (ex: Exception) {
                when {
                    ex.isConnection() -> {
                        Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $ex")
                        try {
                            scheduleTaskToRetryUpdatingUserEvents(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_CONNECTION_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Tracking:scheduleTaskToRetryUpdatingUserEvents: failed ⛔ + next 🔁: $ex"
                            )
                        } finally {
                            break
                        }
                    }

                    else -> {
                        Logger.d(TAG, "Tracking: failed ⛔ + next 🔁: $ex")
                        try {
                            scheduleTaskToRetryUpdatingUserEvents(durationInMillis = PolarConstants.API.DELAY_TO_RETRY_API_REQUEST_IF_SERVER_ERROR_IN_MILLIS) //5s
                        } catch (ex: Throwable) {
                            Logger.d(
                                TAG,
                                "Tracking:scheduleTaskToRetryUpdatingUserEvents: failed ⛔ + next 🔁: $ex"
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
}