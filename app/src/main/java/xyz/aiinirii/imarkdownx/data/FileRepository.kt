package xyz.aiinirii.imarkdownx.data

import android.util.Log
import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.FileDao
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */

private const val TAG = "FileRepository"

class FileRepository(private val fileDao: FileDao) {

    val files: LiveData<List<File>> = fileDao.loadAllFiles()

    suspend fun insert(file: File) {
        Log.d(TAG, "insert: begin to insert file")
        fileDao.insertFile(file)
        Log.d(TAG, "insert: successfully insert file")
    }

    suspend fun update(file: File) {
        Log.d(TAG, "update: begin to update the file")
        fileDao.updateFile(file)
        Log.d(TAG, "update: successfully update the file")
    }

    fun get(fileId: Long): LiveData<File> = fileDao.findFileById(fileId)


}