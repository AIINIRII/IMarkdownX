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

    var files: LiveData<List<File>> = fileDao.loadAllFiles()

    suspend fun insert(file: File): Long {
        Log.d(TAG, "insert: insert file")
        return fileDao.insertFile(file)
    }

    suspend fun update(file: File) {
        Log.d(TAG, "update: begin to update the file")
        fileDao.updateFile(file)
    }

    fun get(fileId: Long): LiveData<File> = fileDao.findFileById(fileId)

    fun refresh(){
        files = fileDao.loadAllFiles()
    }

    suspend fun delete(deleteFile: File) {
        fileDao.deleteFile(deleteFile)
    }

}