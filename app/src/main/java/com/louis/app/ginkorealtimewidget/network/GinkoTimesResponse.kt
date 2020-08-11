package com.louis.app.ginkorealtimewidget.network

import com.louis.app.ginkorealtimewidget.model.TimeWrapper
import com.squareup.moshi.Json

// Original API response (URL: https://api.ginko.voyage/TR/getListeTemps.do?[args]
data class GinkoTimesResponse(
    @Json(name = "ok") val isSuccessful: Boolean = false,
    @Json(name = "objets") val data: List<TimeWrapper>
)
