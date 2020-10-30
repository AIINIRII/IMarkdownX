package xyz.aiinirii.imarkdownx.data

import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.domain.FileItem

/**
 *
 * @author AIINIRII
 */
interface FileCatalogDataSource {

    fun getFileCatalog():LiveData<List<FileItem>>
}