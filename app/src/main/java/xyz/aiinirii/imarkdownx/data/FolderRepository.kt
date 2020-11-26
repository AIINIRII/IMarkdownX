package xyz.aiinirii.imarkdownx.data

import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.FolderDao
import xyz.aiinirii.imarkdownx.entity.Folder

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

}
