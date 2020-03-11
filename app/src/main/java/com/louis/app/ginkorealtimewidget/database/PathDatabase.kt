package com.louis.app.ginkorealtimewidget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.louis.app.ginkorealtimewidget.model.Path

@Database(entities = [Path::class], version = 1)
abstract class PathDatabase() : RoomDatabase() {

    companion object {
        private var INSTANCE: PathDatabase? = null

        fun getInstance(context: Context): PathDatabase? {
            if (INSTANCE == null) {
                synchronized(PathDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                                    PathDatabase::class.java, "path.db")
                            .build()
                }
            }

            return INSTANCE
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}