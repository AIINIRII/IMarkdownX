package xyz.aiinirii.imarkdownx.data.mock

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay
import xyz.aiinirii.imarkdownx.data.FileCatalogDataSource
import xyz.aiinirii.imarkdownx.domain.FileItem

/**
 *
 * @author AIINIRII
 */
class FileCatalogMockRepository : FileCatalogDataSource {
    companion object {
        val FILE_ITEMS = mutableListOf(
            FileItem("file1", "2000-01-01", "null"),
            FileItem("file2", "2000-01-02", "null"),
            FileItem("file3", "2000-01-03", "null"),
            FileItem("file4", "2000-01-04", "null"),
            FileItem("file5", "2000-01-05", "null"),
            FileItem("file6", "2000-01-06", "null"),
            FileItem("file7", "2000-01-07", "null"),
            FileItem("file8", "2000-01-08", "null"),
            FileItem("file9", "2000-01-09", "null"),
            FileItem("file10", "2000-01-10", "null"),
        )
    }

    override fun getFileCatalog(): LiveData<List<FileItem>> = liveData {
        delay(3000L)
        emit(FILE_ITEMS)
    }
}