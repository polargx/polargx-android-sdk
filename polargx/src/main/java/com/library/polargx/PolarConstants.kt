package com.library.polargx

import java.util.TimeZone

object PolarConstants {

    const val FINGERPRINT = "AndroidSDK"

    // Delay in milliseconds to retry API request if rate limited (429)
    object API {
        var DELAY_TO_UPDATE_PROFILE_DURATION_IN_MILLIS =
            if (Configuration.Env.isDebugging) 0 else 1000L
        var DELAY_TO_RETRY_API_REQUEST_IF_SERVER_ERROR_IN_MILLIS =
            if (Configuration.Env.isDebugging) 10000L else 300000L
        var DELAY_TO_RETRY_API_REQUEST_IF_TIME_LIMITS_IN_MILLIS = 5000L // 5 seconds
        var DELAY_TO_RETRY_API_REQUEST_IF_CONNECTION_ERROR_IN_MILLIS = 5000L // 5 seconds
    }

    object Koin {
        const val BASE_URL = "PolarConstants.Koin.BASE_URL"
        const val X_API_KEY = "PolarConstants.Koin.X_API_KEY"
        const val SERVER_CLIENT_ID = "PolarConstants.SERVER_CLIENT_ID"
        const val SHARED_PREFS = "PolarConstants.Koin.SHARED_PREFS"
        const val RATE_LIMIT_HTTP_CLIENT = "PolarConstants.RATE_LIMIT_HTTP_CLIENT"
        const val THREAD_LOCKER = "PolarConstants.THREAD_LOCKER"
    }

    const val RATE_LIMIT_WAITED_ACTION = "PolarConstants.RATE_LIMIT_WAITED_ACTION"

    object DateTime {
        val utcTimeZone = TimeZone.getTimeZone("GMT+00:00")

        const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val BackendDateTimeMsFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }


    object Local {
        object Prefers {
            const val FIRST_TIME_KEY = "first_time"
            const val INSTALL_TIME_KEY = "install_time"

            object Event {
                const val EVENTS_KEY = "EVENTS_KEY"
            }

            object Link {
                const val LINK_DATA_KEY = "LINK_DATA_KEY"
            }
        }
    }

    object InternalEvent {
        const val APP_OPEN = "app_open"
        const val APP_CLOSE = "app_close"
        const val APP_ACTIVE = "app_active"
        const val APP_INACTIVE = "app_inactive"
        const val APP_TERMINATE = "app_terminate"

        const val USER_SESSION_START = "user_session_start"

        const val LINK_CLICK = "link_click"

        const val PUSH_OPEN = "push_open"
    }

    object PolarEventKey {
        const val Email = "email"
        const val Name = "name"
        const val FirstName = "firstName"
        const val LastName = "lastName"
    }
}