package xyz.aiinirii.imarkdownx.ui.edit.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val fileContent = MutableLiveData<String>()
    val fileName = MutableLiveData<String>()

    private val _renderFile = MutableLiveData<Boolean>().apply {
        this.postValue(false)
    }
    val renderFile: LiveData<Boolean>
        get() = _renderFile

    init {
        val fileDao = AppDatabase.getDatabase(IMarkdownXApplication.context).fileDao()
        repository = FileRepository(fileDao)
        Log.d(TAG, "init: init successfully")
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
                viewModelScope.launch {
                    repository.update(it)
                }
                Log.d(TAG, "saveFile: successfully save the file")
            }
        } else {
            Log.d(TAG, "saveFile: begin to create file")
            val currentFile =
                File(fileName.value.toString(), simpleDateFormat.format(Date()), fileContent.value.toString())
            Log.d(TAG, "saveFile: set content: ${currentFile.content}, set date: ${currentFile.date}")
            viewModelScope.launch {
                repository.insert(currentFile)
            }
            Log.d(TAG, "saveFile: successfully save the file")
        }
    }


}