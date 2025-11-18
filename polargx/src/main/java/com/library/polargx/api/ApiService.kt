package com.library.polargx.api

import android.content.Context
import com.library.polargx.data.tracking.remote.update_user.UpdateUserRequest
import com.library.polargx.data.tracking.remote.track_event.TrackEventRequest
import com.library.polargx.models.LinkClickModel
import com.library.polargx.models.LinkDataModel
import com.library.polargx.data.links.local.update_link.UpdateLinkClickRequest
import com.library.polargx.data.links.remote.track_link.TrackLinkClickRequest

interface ApiService {

    // -------------------- Links --------------------

    suspend fun getLinkData(domain: String?, slug: String?): LinkDataModel?

    suspend fun trackLinkClick(request: TrackLinkClickRequest?): LinkClickModel?

    suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?)

    suspend fun matchLinkClick(fingerprint: String?): LinkClickModel?

    // -------------------- Other --------------------

    suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean
}