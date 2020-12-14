package xyz.aiinirii.imarkdownx.data

import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.db.AppDatabase

object DatabaseRepository {

    fun fileDao() = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
    fun userDao() = AppDatabase.getDatabase(IMarkdownXApplication.context).userDao()
    fun folderDao() = AppDatabase.getDatabase(IMarkdownXApplication.context).folderDao()
    fun todoDao() = AppDatabase.getDatabase(IMarkdownXApplication.context).todoDao()
}