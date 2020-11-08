package xyz.aiinirii.imarkdownx.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.aiinirii.imarkdownx.data.dao.FileDao
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
@Database(version = 1, entities = [File::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "imarkdownx_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}