package com.louis.app.ginkorealtimewidget.database

import androidx.lifecycle.LiveData
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

    /*@Query("SELECT * FROM path WHERE start_point_name=:busStopName")
    suspend fun getPathForStartPoint(busStopName: String)*/

    @Query("SELECT * FROM path WHERE is_current_path = 0")
    fun getAllPathsButCurrentPath(): LiveData<List<Path>>

    @Query("SELECT * FROM path WHERE is_current_path = 1")
    fun getWidgetPath(): LiveData<Path>

    @Query("SELECT * FROM path WHERE is_current_path = 1")
    suspend fun getWidgetPathNotLive(): Path

    @Query("UPDATE path SET is_current_path = 0 WHERE is_current_path = 1")
    suspend fun resetWidgetPath()

    @Query("SELECT * FROM path")
    fun getAllPaths(): LiveData<List<Path>>
}