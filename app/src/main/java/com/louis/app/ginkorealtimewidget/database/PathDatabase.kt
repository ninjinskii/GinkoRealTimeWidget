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
        private var instance: PathDatabase? = null

        fun getInstance(context: Context): PathDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): PathDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PathDatabase::class.java,
                "path.db"
            ).build()
        }
    }
}