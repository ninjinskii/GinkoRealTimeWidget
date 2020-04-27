package com.louis.app.ginkorealtimewidget.model

import com.squareup.moshi.Json
import retrofit2.http.GET

// Représente une ligne de bus
data class Line(@Json(name = "id") val id : String,
                @Json(name = "libellePublic") val publicWayInfo: String,
                @Json(name = "numLignePublic") val publicName: String,
                @Json(name = "couleurFond") val backgroundColor: String,
                @Json(name = "couleurTexte") val textColor: String,
                @Json(name = "variantes") val oneWayLines: List<OneWayLine>
)