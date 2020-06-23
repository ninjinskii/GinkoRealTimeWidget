package com.louis.app.ginkorealtimewidget.model

import com.squareup.moshi.Json

data class TimeWrapper(
    @Json(name = "listeTemps") val timeList: List<Time>,
    @Json(name = "nomExact") val verifiedBusStopName: String
)