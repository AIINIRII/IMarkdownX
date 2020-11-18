package xyz.aiinirii.imarkdownx.ui.edit.privacy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File

class PrivacyViewModel : ViewModel() {

    private val repository: FileRepository

    val files: LiveData<List<File>>

    init {
        val fileDao = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
        repository = FileRepository(fileDao)
        files = repository.lockedFile
    }

    fun unlockItem(position: Int) {
        viewModelScope.launch {
            val lockedFile = files.value?.get(position)
            if (lockedFile != null) {
                repository.unlock(lockedFile)
            }
            repository.refresh()
        }
    }

    fun deleteItem(position:Int) {
        viewModelScope.launch {
            val deleteFile = files.value?.get(position)
            if (deleteFile != null) {
                repository.delete(deleteFile)
            }
            repository.refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
        }
    }
    /**
     * Factory for [PrivacyViewModel]
     */
    object PrivacyViewModelFactory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = PrivacyViewModel() as T
    }
}