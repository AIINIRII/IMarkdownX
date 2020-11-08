package xyz.aiinirii.imarkdownx.data.mock

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.dao.FileDao
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
class FileMockRepository : FileRepository() {
    companion object {
        val FILE_ITEMS = mutableListOf(
            File("file1", "2000-01-01", "null"),
            File("file2", "2000-01-02", "null"),
            File("file3", "2000-01-03", "null"),
            File("file4", "2000-01-04", "null"),
            File("file5", "2000-01-05", "null"),
            File("file6", "2000-01-06", "null"),
            File("file7", "2000-01-07", "null"),
            File("file8", "2000-01-08", "null"),
            File("file9", "2000-01-09", "null"),
            File("file10", "2000-01-10", "null"),
        )
    }

    override suspend fun getFileCatalog(): LiveData<List<File>> = liveData {
        delay(3000L)
        emit(FILE_ITEMS)
    }
}