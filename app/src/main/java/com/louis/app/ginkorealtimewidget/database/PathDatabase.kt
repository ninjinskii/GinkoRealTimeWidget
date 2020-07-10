package com.louis.app.ginkorealtimewidget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.louis.app.ginkorealtimewidget.model.Path

@Database(entities = [Path::class], version = 5, exportSchema = false)
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
                    "path.db"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

//@Database(entities = [GardenPlanting::class, Plant::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun gardenPlantingDao(): GardenPlantingDao
//    abstract fun plantDao(): PlantDao
//
//    companion object {
//
//        // For Singleton instantiation
//        @Volatile private var instance: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase {
//            return instance ?: synchronized(this) {
//                instance ?: buildDatabase(context).also { instance = it }
//            }
//        }
//
//        // Create and pre-populate the database. See this article for more details:
//        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
//        private fun buildDatabase(context: Context): AppDatabase {
//            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
//                        WorkManager.getInstance(context).enqueue(request)
//                    }
//                })
//                .build()
//        }
//    }
//}