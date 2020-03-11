package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

// Terminus d'un Path
data class EndPoint(@ColumnInfo(name = "end_point_name") var name: String) : BusStop()