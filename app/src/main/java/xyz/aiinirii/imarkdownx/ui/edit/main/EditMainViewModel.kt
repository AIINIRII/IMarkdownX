package xyz.aiinirii.imarkdownx.ui.edit.main

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "EditMainViewModel"

class EditMainViewModel : ViewModel() {

    private val repository: FileRepository
    lateinit var file: LiveData<File>
        private set
    var folderId: Long = -1L
    val fileContent = MutableLiveData<String>()
    val fileName = MutableLiveData<String>()
    val locked = MutableLiveData<Boolean>()
    val isSaved = MutableLiveData<Boolean>().apply {
        this.postValue(false)
    }
    private val _renderFile = MutableLiveData<Boolean>().apply {
        this.postValue(false)
    }
    val renderFile: LiveData<Boolean>
        get() = _renderFile

    val tableRow = MutableLiveData<String>()
    val tableCol = MutableLiveData<String>()
    val confirmTableState = MutableLiveData<Boolean>()
    val cancelTableCreateState = MutableLiveData<Boolean>()

    val deleteState = MutableLiveData<Boolean>()
    val headingState = MutableLiveData<Boolean>()
    val quoteState = MutableLiveData<Boolean>()
    val boldState = MutableLiveData<Boolean>()
    val italicState = MutableLiveData<Boolean>()
    val fileState = MutableLiveData<Boolean>()
    val codeState = MutableLiveData<Boolean>()
    val linkState = MutableLiveData<Boolean>()
    val linkAltState = MutableLiveData<Boolean>()
    val listState = MutableLiveData<Boolean>()
    val tableState = MutableLiveData<Boolean>()
    val hrState = MutableLiveData<Boolean>()

    private val _imageUri = MutableLiveData<String>()
    val imageUri: LiveData<String>
        get() = _imageUri

    val loadFromInternetState = MutableLiveData<Boolean>()
    val loadFromPhotoState = MutableLiveData<Boolean>()

    init {
        val fileDao = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
        repository = FileRepository(fileDao)
        Log.d(TAG, "init: init successfully")
    }

    fun setImageUri(uri: String) {
        _imageUri.postValue(uri)
    }

    fun delete() {
        deleteState.postValue(true)
    }

    fun heading() {
        headingState.postValue(true)
    }

    fun quote() {
        quoteState.postValue(true)
    }

    fun bold() {
        boldState.postValue(true)
    }

    fun italic() {
        italicState.postValue(true)
    }

    fun hr() {
        hrState.postValue(true)
    }

    fun image() {
        fileState.postValue(true)
    }

    fun code() {
        codeState.postValue(true)
    }

    fun link() {
        linkState.postValue(true)
    }

    fun linkAlt() {
        linkAltState.postValue(true)
    }

    fun list() {
        listState.postValue(true)
    }

    fun table() {
        tableState.postValue(true)
    }

    fun cancelTableCreate() {
        cancelTableCreateState.postValue(true)
    }

    fun confirmTableCreate() {
        confirmTableState.postValue(true)
    }

    fun loadFromInternet() {
        loadFromInternetState.postValue(true)
    }

    fun loadFromPhoto() {
        loadFromPhotoState.postValue(true)
    }

    internal fun getFileById(fileId: Long?): LiveData<File>? = if (fileId != null) {
        file = repository.get(fileId)
        Log.d(TAG, "getFileById: successfully get file: ${file.value.toString()}")
        file
    } else {
        Log.d(TAG, "getFileById: fail to get file")
        null
    }

    fun renderFile() {
        _renderFile.postValue(true)
    }

    fun saveFile() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        if (this::file.isInitialized) {
            Log.d(TAG, "saveFile: get now file: ${file.value.toString()}")
            val currentFile: File? = file.value
            currentFile?.let {
                Log.d(TAG, "saveFile: begin to save file")
                it.name = fileName.value.toString()
                it.content = fileContent.value.toString()
                it.date = simpleDateFormat.format(Date())
                Log.d(TAG, "saveFile: set content: ${it.content}, set date: ${it.date}")
                viewModelScope.launch(Dispatchers.IO) {
                    repository.update(it)
                }
                Log.d(TAG, "saveFile: successfully save the file")
            }
        } else {
            if (folderId == -1L) {
                throw IllegalStateException("the folder id haven't been set")
            }
            val currentFile =
                File(
                    fileName.value ?: "",
                    simpleDateFormat.format(Date()),
                    fileContent.value ?: "",
                    locked.value ?: false
                )
            currentFile.folderId = this@EditMainViewModel.folderId
            Log.d(TAG, "saveFile: begin to create file: $currentFile")
            Log.d(TAG, "saveFile: set content: ${currentFile.content}, set date: ${currentFile.date}")
            GlobalScope.launch(Dispatchers.IO) {
                currentFile.id = repository.insert(currentFile)
                Log.d(TAG, "saveFile: successfully save the file with id: ${currentFile.id}")
                file = liveData { emit(currentFile) }
            }
        }
        isSaved.postValue(true)
    }

}