package xyz.aiinirii.imarkdownx.ui.edit

import android.graphics.Color
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication.Companion.context
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.FolderRepository
import xyz.aiinirii.imarkdownx.data.UserRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File
import xyz.aiinirii.imarkdownx.entity.Folder
import java.util.*

// todo: save the folder info in this class and create one if none.
/**
 * view model for edit page
 * @constructor
 */
class EditViewModel : ViewModel() {
    private val fileRepository: FileRepository
    private val userRepository: UserRepository
    private val folderRepository: FolderRepository

    val folders = MutableLiveData<LiveData<List<Folder>>>()

    val privatePasswordVerified = MutableLiveData<Int>().apply { this.postValue(0) }

    private val _isHavePrivatePassword = MutableLiveData<Int>().apply { this.postValue(0) }
    val isHavePrivatePassword: LiveData<Int>
        get() = _isHavePrivatePassword

    private val _filesInFolderInitialized = MutableLiveData<Boolean>()
    val filesInFolderInitialized: LiveData<Boolean>
        get() = _filesInFolderInitialized

    var folder = MutableLiveData<LiveData<Folder?>>()
    lateinit var filesInFolder: LiveData<List<File>>

    val toolbarTitle = MutableLiveData<String>()

    init {
        val database = AppDatabase.getDatabase(context)
        val fileDao = database.fileDao()
        val userDao = database.userDao()
        val folderDao = database.folderDao()
        fileRepository = FileRepository(fileDao)
        userRepository = UserRepository(userDao)
        folderRepository = FolderRepository(folderDao)

        folders.postValue(folderRepository.findAllFolders())
        folder.postValue(folderRepository.findFirstFolder())
    }

    suspend fun updateFilesInFolderByFolder(folder: Folder?) {
        this.folder.postValue(liveData {
            emit(folder)
        })
    }

    suspend fun findFilesByFolder(): LiveData<List<File>> {
        val currentFolder: Folder
        if (folder.value!!.value == null) {
            val newFolder =
                Folder(context.getString(R.string.default_folder_name), context.getColor(R.color.colorPrimaryDark))
            val id = folderRepository.insert(newFolder)
            newFolder.id = id
            currentFolder = newFolder
        } else {
            currentFolder = folder.value!!.value!!
        }
        filesInFolder = fileRepository.findUnlockedFilesByFolder(currentFolder)
        _filesInFolderInitialized.postValue(true)
        return filesInFolder
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

    fun initIsHavePrivatePassword() {
        _isHavePrivatePassword.postValue(0)
        privatePasswordVerified.postValue(0)
    }

    fun deleteItem(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteFile = filesInFolder.value?.get(position)
            if (deleteFile != null) {
                fileRepository.delete(deleteFile)
            }
            fileRepository.refresh()
        }
    }

    fun lockItem(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val lockedFile = filesInFolder.value?.get(position)
            if (lockedFile != null) {
                fileRepository.lock(lockedFile)
            }
            fileRepository.refresh()
        }
    }

    private suspend fun havePrivatePassword(userId: Long): Boolean {
        return userRepository.havePrivatePassword(userId)
    }

    suspend fun verifyPrivatePassword(userLocalId: Long, privatePassword: String): Boolean {
        return userRepository.verifyPrivatePassword(userLocalId, privatePassword)
    }


    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepository.refresh()
        }
    }

    fun createFolder(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val rand = Random(System.currentTimeMillis())
            val folder = Folder(folderName, Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)))
            folderRepository.insert(folder)
        }
    }

    fun deleteFolderItem(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteFolder = folders.value!!.value?.get(position)
            if (deleteFolder != null) {
                folderRepository.delete(deleteFolder)
            }
            folders.postValue(folderRepository.findAllFolders())
        }
    }

    fun changeFolderColor(position: Int, red: Int, green: Int, blue: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateFolder = folders.value!!.value?.get(position)
            updateFolder?.color = Color.rgb(red, green, blue)
            if (updateFolder != null) {
                folderRepository.update(updateFolder)
            }
            folders.postValue(folderRepository.findAllFolders())
        }
    }

    fun changeFolderName(position: Int, folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateFolder = folders.value!!.value?.get(position)
            updateFolder?.name = folderName
            if (updateFolder != null) {
                folderRepository.update(updateFolder)
            }
            folders.postValue(folderRepository.findAllFolders())
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