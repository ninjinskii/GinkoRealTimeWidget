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

    @Query("SELECT * FROM path WHERE is_current_path = 0 AND is_soft_deleted = 0")
    fun getAllPathsButCurrentPath(): LiveData<List<Path>>

    @Query("SELECT * FROM path WHERE is_current_path = 1")
    fun getWidgetPath(): LiveData<Path>

    @Query("SELECT * FROM path WHERE is_current_path = 1")
    suspend fun getWidgetPathNotLive(): Path

    @Query("UPDATE path SET is_current_path = 0 WHERE is_current_path = 1")
    suspend fun resetWidgetPath()

    @Query("SELECT * FROM path WHERE is_soft_deleted = 0")
    fun getAllPaths(): LiveData<List<Path>>

    // This transaction prevent DiffUtil to trigger twice, causing weird recycler view animations
    @Transaction
    suspend fun setNewWidgetPath(path: Path) {
        resetWidgetPath()
        updatePath(path)
    }

    @Query("UPDATE path SET is_soft_deleted = NOT is_soft_deleted WHERE path.id=:pathId")
    suspend fun toggleSoftDeletePath(pathId: Long)

    @Query("DELETE FROM path WHERE is_soft_deleted = 1")
    suspend fun purgeSoftDeletedPaths()
}