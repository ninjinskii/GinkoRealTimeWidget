package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

data class StartPoint(
        @ColumnInfo(name = "start_point_name") override val name: String
        ) : BusStop(name)