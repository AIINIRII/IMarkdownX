package xyz.aiinirii.imarkdownx.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
@Dao
interface FileDao {

    @Insert
    suspend  fun insertFile(file: File): Long

    @Update
    suspend fun updateFile(newFile: File)

    @Query("select * from File")
    fun loadAllFiles(): LiveData<List<File>>

    @Delete
    suspend fun deleteFile(deleteFile: File)

    @Query("select * from File where id=:fileId")
    fun findFileById(fileId: Long): LiveData<File>
}