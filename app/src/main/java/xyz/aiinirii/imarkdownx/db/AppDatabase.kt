package xyz.aiinirii.imarkdownx.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import xyz.aiinirii.imarkdownx.data.dao.FileDao
import xyz.aiinirii.imarkdownx.data.dao.FolderDao
import xyz.aiinirii.imarkdownx.data.dao.TodoDao
import xyz.aiinirii.imarkdownx.data.dao.UserDao
import xyz.aiinirii.imarkdownx.entity.File
import xyz.aiinirii.imarkdownx.entity.Folder
import xyz.aiinirii.imarkdownx.entity.Todo
import xyz.aiinirii.imarkdownx.entity.User

/**
 *
 * @author AIINIRII
 */
@Database(version = 6, entities = [Folder::class, File::class, User::class, Todo::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao
    abstract fun userDao(): UserDao
    abstract fun folderDao(): FolderDao
    abstract fun todoDao(): TodoDao

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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}