package com.louis.app.ginkorealtimewidget.model

import com.squareup.moshi.Json

class VariantWithoutLine(@Json(name = "id") val idVariant: String,
                         @Json(name = "nom") val name: String)