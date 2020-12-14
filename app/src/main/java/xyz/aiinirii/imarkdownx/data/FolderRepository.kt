package xyz.aiinirii.imarkdownx.data

import android.graphics.Color
import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.FolderDao
import xyz.aiinirii.imarkdownx.entity.Folder
import java.util.*

/**
 *
 * @author AIINIRII
 */
private const val TAG = "FolderRepository"

class FolderRepository(private val folderDao: FolderDao) {

    /**
     * find all folders
     * @return LiveData<List<Folder>>
     */
    fun findAllFolders(): LiveData<List<Folder>> {
        return folderDao.findAllFolders()
    }

    fun findFirstFolder(): LiveData<Folder?> {
        return folderDao.findFirstFolder()
    }

    suspend fun insert(newFolder: Folder): Long {
        return folderDao.insert(newFolder)
    }

    suspend fun delete(deleteFolder: Folder) {
        folderDao.delete(deleteFolder)
    }

    suspend fun update(updateFolder: Folder) {
        folderDao.update(updateFolder)
    }

    suspend fun createFolderIfNotExist(folderId: Long) {
        val folderFound = folderDao.findById(folderId)
        val rand = Random()
        if (folderFound == null) {
            folderDao.insert(
                Folder(
                    "remote folder",
                    Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
                )
            )
        }
    }

}
