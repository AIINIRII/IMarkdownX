package xyz.aiinirii.imarkdownx.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
@Dao
interface FileDao {

    @Insert
    suspend fun insertFile(file: File): Long

    @Update
    suspend fun updateFile(newFile: File)

    @Query("select * from File")
    fun loadAllFiles(): List<File>

    @Query("select COUNT(1) from File")
    fun countFiles(): Int

    @Delete
    suspend fun deleteFile(deleteFile: File)

    @Query("select * from File where id=:fileId")
    fun findFileById(fileId: Long): LiveData<File>

    @Query("select * from File where not locked")
    fun loadAllUnLockedFiles(): LiveData<List<File>>

    @Query("select * from File where locked")
    fun loadAllLockedFiles(): LiveData<List<File>>

    @Query("select File.id, File.name, date, content, locked, remoteId, Folder.id as folderId from File left join Folder on File.folderId = Folder.id where Folder.id=:id and not locked")
    fun loadAllUnLockedFilesByFolderId(id: Long): LiveData<List<File>>

    @Query("select File.id, File.name, date, content, locked, remoteId, Folder.id as folderId from File left join Folder on File.folderId = Folder.id where Folder.id=:id and locked")
    fun loadAllLockedFilesByFolderId(id: Long): LiveData<List<File>>

    @Query("select * from File where remoteId=:id")
    fun getFileByRemoteId(id: Long): File?

    @Query("select * from File where not locked")
    fun loadAllUnLockedFilesFast(): List<File>
}