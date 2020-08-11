package com.louis.app.ginkorealtimewidget.model

import com.squareup.moshi.Json

// Représente une ligne de bus
data class Line(
    @Json(name = "id") var lineId: String,
    @Json(name = "libellePublic") var publicWayInfo: String,
    @Json(name = "numLignePublic") var publicName: String,
    @Json(name = "couleurFond") var backgroundColor: String,
    @Json(name = "couleurTexte") var textColor: String,
    @Json(name = "variantes") var variants: List<Variant>
)
