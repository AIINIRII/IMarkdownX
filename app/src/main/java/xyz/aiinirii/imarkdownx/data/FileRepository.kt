package xyz.aiinirii.imarkdownx.data

import android.util.Log
import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.FileDao
import xyz.aiinirii.imarkdownx.entity.File
import xyz.aiinirii.imarkdownx.entity.Folder

/**
 *
 * @author AIINIRII
 */

private const val TAG = "FileRepository"

class FileRepository(private val fileDao: FileDao) {

    var unlockedFile: LiveData<List<File>> = fileDao.loadAllUnLockedFiles()
    var lockedFile: LiveData<List<File>> = fileDao.loadAllLockedFiles()

    suspend fun insert(file: File): Long {
        Log.d(TAG, "insert: insert file")
        return fileDao.insertFile(file)
    }

    suspend fun update(file: File) {
        Log.d(TAG, "update: begin to update the file")
        fileDao.updateFile(file)
    }

    suspend fun lock(file: File) {
        Log.d(TAG, "lock: lock the file")
        file.locked = true
        fileDao.updateFile(file)
    }

    fun get(fileId: Long): LiveData<File> = fileDao.findFileById(fileId)

    fun refresh() {
        unlockedFile = fileDao.loadAllUnLockedFiles()
        lockedFile = fileDao.loadAllLockedFiles()
    }

    suspend fun delete(deleteFile: File) {
        fileDao.deleteFile(deleteFile)
    }

    suspend fun unlock(file: File) {
        Log.d(TAG, "unLock: unlock the file")
        file.locked = false
        fileDao.updateFile(file)
    }

    fun findUnlockedFilesByFolder(folder: Folder): LiveData<List<File>> {
        return fileDao.loadAllUnLockedFilesByFolderId(folder.id)
    }
    fun findLockedFilesByFolder(folder: Folder): LiveData<List<File>> {
        return fileDao.loadAllLockedFilesByFolderId(folder.id)
    }

}