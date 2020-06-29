package com.louis.app.ginkorealtimewidget.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Représente un trajet de bus
@Entity(tableName = "path")
data class Path(
    var startingPoint: String,
    var endingPoint: String,
    @Embedded var line: Line,
    @ColumnInfo(name = "is_start_point_natural_way") var isStartPointNaturalWay: Boolean,
    @ColumnInfo(name = "is_current_path") var isCurrentPath: Int = 0,
    @ColumnInfo(name = "is_start_point_used_for_widget") var isStartPointUsedForWidget: Int = 1,
    @ColumnInfo(name = "is_soft_deleted") var isSoftDeleted: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun getName() = "$startingPoint <> $endingPoint"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Path

        if (startingPoint != other.startingPoint) return false
        if (endingPoint != other.endingPoint) return false
        if (line != other.line) return false
        if (isStartPointNaturalWay != other.isStartPointNaturalWay) return false
        if (isCurrentPath != other.isCurrentPath) return false
        if (isStartPointUsedForWidget != other.isStartPointUsedForWidget) return false
        if (isSoftDeleted != other.isSoftDeleted) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startingPoint.hashCode()
        result = 31 * result + endingPoint.hashCode()
        result = 31 * result + line.hashCode()
        result = 31 * result + isStartPointNaturalWay.hashCode()
        result = 31 * result + isCurrentPath
        result = 31 * result + isStartPointUsedForWidget
        result = 31 * result + isSoftDeleted
        result = 31 * result + id.hashCode()
        return result
    }
}