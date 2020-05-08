package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

// Terminus d'un Path
data class EndPoint(@ColumnInfo(name = "ending_point") var endName: String)