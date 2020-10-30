package xyz.aiinirii.imarkdownx.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.data.FileCatalogDataSource
import xyz.aiinirii.imarkdownx.data.mock.FileCatalogMockRepository

/**
 * view model for edit page
 * @property dataSource FileCatalogDataSource
 * @property fileCatalog LiveData<List<FileItem>>
 * @constructor
 */
class EditViewModel(
    private val dataSource: FileCatalogDataSource
) : ViewModel() {
    val fileCatalog = dataSource.getFileCatalog()
}

/**
 * Factory for [EditViewModel]
 */
object EditViewModelFactory:ViewModelProvider.Factory{

    private val dataSource = FileCatalogMockRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = EditViewModel(dataSource) as T
}