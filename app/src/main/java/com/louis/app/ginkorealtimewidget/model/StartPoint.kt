package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

// Point de départ d'un Path
data class StartPoint(@ColumnInfo(name = "starting_point") var startName: String) {
    fun getName() = startName
}