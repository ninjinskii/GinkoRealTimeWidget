package com.louis.app.ginkorealtimewidget.database

import androidx.room.*
import com.louis.app.ginkorealtimewidget.model.Path

@Dao
interface PathDao {

    @Insert
    suspend fun insertPath(path: Path)

    @Update
    suspend fun updatePath(path: Path)

    @Delete
    suspend fun deletePath(path: Path)

    @Query("SELECT * FROM path WHERE name=:busStopName")
    suspend fun getPathForStartPoint(busStopName: String)
}