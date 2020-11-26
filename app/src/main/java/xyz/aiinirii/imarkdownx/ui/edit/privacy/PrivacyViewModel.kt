package xyz.aiinirii.imarkdownx.ui.edit.privacy

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.FolderRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File
import xyz.aiinirii.imarkdownx.entity.Folder

class PrivacyViewModel : ViewModel() {

    private val fileRepository: FileRepository
    private val folderRepository: FolderRepository


    private val _filesInFolderInitialized = MutableLiveData<Boolean>()
    val filesInFolderInitialized: LiveData<Boolean>
        get() = _filesInFolderInitialized

    var folder = MutableLiveData<LiveData<Folder?>>()
    private val folders: LiveData<List<Folder>>
    lateinit var filesInFolder: LiveData<List<File>>

    init {
        val database = AppDatabase.getDatabase(IMarkdownXApplication.context)
        val fileDao = database.fileDao()
        val folderDao = database.folderDao()
        fileRepository = FileRepository(fileDao)
        folderRepository = FolderRepository(folderDao)
        folders = folderRepository.findAllFolders()
        folder.postValue(folderRepository.findFirstFolder())
    }

    suspend fun findFilesByFolder(): LiveData<List<File>> {
        val currentFolder: Folder
        if (folder.value!!.value == null) {
            val newFolder =
                Folder(
                    IMarkdownXApplication.context.getString(R.string.default_folder_name),
                    IMarkdownXApplication.context.getColor(
                        R.color.colorPrimaryDark
                    )
                )
            val id = folderRepository.insert(newFolder)
            newFolder.id = id
            currentFolder = newFolder
        } else {
            currentFolder = folder.value!!.value!!
        }
        filesInFolder = fileRepository.findLockedFilesByFolder(currentFolder)
        _filesInFolderInitialized.postValue(true)
        return filesInFolder
    }

    fun unlockItem(position: Int) {
        viewModelScope.launch {
            val lockedFile = filesInFolder.value?.get(position)
            if (lockedFile != null) {
                fileRepository.unlock(lockedFile)
            }
            findFilesByFolder()
        }
    }

    fun deleteItem(position: Int) {
        viewModelScope.launch {
            val deleteFile = filesInFolder.value?.get(position)
            if (deleteFile != null) {
                fileRepository.delete(deleteFile)
            }
            findFilesByFolder()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            fileRepository.refresh()
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