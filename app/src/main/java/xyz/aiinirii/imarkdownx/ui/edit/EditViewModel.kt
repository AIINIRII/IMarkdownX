package xyz.aiinirii.imarkdownx.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File

/**
 * view model for edit page
 * @property dataSource FileCatalogDataSource
 * @property fileCatalog LiveData<List<FileItem>>
 * @constructor
 */
class EditViewModel : ViewModel() {
    private val repository: FileRepository

    val files: LiveData<List<File>>

    init {
        val fileDao = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
        repository = FileRepository(fileDao)
        files = repository.files
    }
}

/**
 * Factory for [EditViewModel]
 */
object EditViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = EditViewModel() as T
}