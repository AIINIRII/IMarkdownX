package xyz.aiinirii.imarkdownx.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.aiinirii.imarkdownx.entity.Folder

/**
 *
 * @author AIINIRII
 */
@Dao
interface FolderDao {

    @Insert
    suspend fun insert(folder: Folder): Long

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("select * from folder")
    fun findAllFolders(): LiveData<List<Folder>>

    @Query("select * from folder order by id limit 1")
    fun findFirstFolder(): LiveData<Folder?>

    @Query("select * from folder where id=:folderId")
    fun findById(folderId: Long): Folder?
}