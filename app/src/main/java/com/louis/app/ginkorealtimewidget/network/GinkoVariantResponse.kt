package com.louis.app.ginkorealtimewidget.network

import com.louis.app.ginkorealtimewidget.model.VariantWithoutLine
import com.squareup.moshi.Json

class GinkoVariantResponse (@Json(name = "ok") val isSuccessful: Boolean,
                            @Json(name = "objets") val variants: List<VariantWithoutLine>)