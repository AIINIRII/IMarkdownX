package xyz.aiinirii.imarkdownx.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.UserRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File

/**
 * view model for edit page
 * @property dataSource FileCatalogDataSource
 * @property fileCatalog LiveData<List<FileItem>>
 * @constructor
 */
class EditViewModel : ViewModel() {
    private val fileRepository: FileRepository
    private val userRepository: UserRepository

    val files: LiveData<List<File>>

    init {
        val fileDao = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
        val userDao = AppDatabase.getDatabase(IMarkdownXApplication.context).userDao()
        fileRepository = FileRepository(fileDao)
        userRepository = UserRepository(userDao)
        files = fileRepository.unlockedFile
    }

    fun deleteItem(position: Int) {
        viewModelScope.launch {
            val deleteFile = files.value?.get(position)
            if (deleteFile != null) {
                fileRepository.delete(deleteFile)
            }
            fileRepository.refresh()
        }
    }

    fun lockItem(position: Int) {
        viewModelScope.launch {
            val lockedFile = files.value?.get(position)
            if (lockedFile != null) {
                fileRepository.lock(lockedFile)
            }
            fileRepository.refresh()
        }
    }

    suspend fun havePrivatePassword(userId: Long): Boolean {
        return userRepository.havePrivatePassword(userId)
    }

    suspend fun verifyPrivatePassword(userLocalId: Long, privatePassword: String) =
        userRepository.verifyPrivatePassword(userLocalId, privatePassword)


    fun refresh() {
        viewModelScope.launch {
            fileRepository.refresh()
        }
    }
}

/**
 * Factory for [EditViewModel]
 */
object EditViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = EditViewModel() as T
}