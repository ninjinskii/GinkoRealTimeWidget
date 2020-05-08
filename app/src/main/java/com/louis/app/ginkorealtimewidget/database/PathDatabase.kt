package com.louis.app.ginkorealtimewidget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.louis.app.ginkorealtimewidget.model.Path

@Database(entities = [Path::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PathDatabase : RoomDatabase() {

    abstract fun pathDao(): PathDao

    companion object {
        @Volatile
        private var INSTANCE: PathDatabase? = null

        fun getInstance(context: Context): PathDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                PathDatabase::class.java,
                                "path.db")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}