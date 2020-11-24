package xyz.aiinirii.imarkdownx.ui.edit

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.UserRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File

/**
 * view model for edit page
 * @constructor
 */
class EditViewModel : ViewModel() {
    private val fileRepository: FileRepository
    private val userRepository: UserRepository

    val files: LiveData<List<File>>

    val privatePasswordVerified = MutableLiveData<Int>().apply { this.postValue(0) }

    private val _isHavePrivatePassword = MutableLiveData<Int>().apply { this.postValue(0) }
    val isHavePrivatePassword: LiveData<Int>
        get() = _isHavePrivatePassword

    init {
        val database = AppDatabase.getDatabase(IMarkdownXApplication.context)
        val fileDao = database.fileDao()
        val userDao = database.userDao()
        fileRepository = FileRepository(fileDao)
        userRepository = UserRepository(userDao)
        files = fileRepository.unlockedFile
    }

    fun checkPrivatePassword(userLocalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (havePrivatePassword(userLocalId)) {
                _isHavePrivatePassword.postValue(1)
            } else {
                _isHavePrivatePassword.postValue(2)
            }
        }
    }

    fun deleteItem(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteFile = files.value?.get(position)
            if (deleteFile != null) {
                fileRepository.delete(deleteFile)
            }
            fileRepository.refresh()
        }
    }

    fun lockItem(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val lockedFile = files.value?.get(position)
            if (lockedFile != null) {
                fileRepository.lock(lockedFile)
            }
            fileRepository.refresh()
        }
    }

    private suspend fun havePrivatePassword(userId: Long): Boolean {
        return userRepository.havePrivatePassword(userId)
    }

    suspend fun verifyPrivatePassword(userLocalId: Long, privatePassword: String) =
        userRepository.verifyPrivatePassword(userLocalId, privatePassword)


    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
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