package com.louis.app.ginkorealtimewidget.model

import com.squareup.moshi.Json

// Repésente un trajet selon l'API, sens aller ou sens retour (voir https://api.ginko.voyage)
data class OneWayLine(@Json(name = "id") val id: String,
                      @Json(name = "sensAller") val naturalWay: Boolean,
                      @Json(name = "destination") val endPointName: String
)