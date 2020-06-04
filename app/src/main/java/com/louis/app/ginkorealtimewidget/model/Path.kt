package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Représente un trajet de bus
@Entity(tableName = "path")
data class Path(
        @Embedded var startingPoint: StartPoint,
        @Embedded var endingPoint: EndPoint,
        @Embedded var line: Line,
        @ColumnInfo(name = "is_start_point_natural_way") var isStartPointNaturalWay: Boolean,
        @ColumnInfo(name = "is_current_path") var isCurrentPath: Int = 0,
        @ColumnInfo(name = "is_start_point_used_for_widget") var isStartPointUsedForWidget: Int = 1){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun getName() = "${startingPoint.startName} <> ${endingPoint.endName}"

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }

        other as Path

        if (id != other.id) return false
        if (startingPoint != other.startingPoint) return false
        if (endingPoint != other.endingPoint) return false
        if (line != other.line) return false
        if (isStartPointNaturalWay != other.isStartPointNaturalWay) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startingPoint.hashCode()
        result = 31 * result + endingPoint.hashCode()
        result = 31 * result + line.hashCode()
        result = 31 * result + isStartPointNaturalWay.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}