package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

// Point de départ d'un Path
data class StartPoint(@ColumnInfo(name = "start_point_name") var name: String) : BusStop()