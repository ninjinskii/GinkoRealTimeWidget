package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo

data class EndPoint(
        @ColumnInfo(name = "end_point_name") override val name: String
) : BusStop(name)