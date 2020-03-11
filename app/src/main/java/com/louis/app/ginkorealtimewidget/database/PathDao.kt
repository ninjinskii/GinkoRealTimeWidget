package com.louis.app.ginkorealtimewidget.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.louis.app.ginkorealtimewidget.model.Path

@Dao
interface PathDao {

    @Insert
    suspend fun insertPath(path: Path)

    @Update
    suspend fun updatePath(path: Path)

    @Delete
    suspend fun deletePath(path: Path)
}