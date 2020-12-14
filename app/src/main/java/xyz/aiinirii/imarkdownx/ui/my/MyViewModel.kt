package xyz.aiinirii.imarkdownx.ui.my

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.aiinirii.imarkdownx.IMarkdownXApplication.Companion.context
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.data.*
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.data.model.TextFullInfoParams
import xyz.aiinirii.imarkdownx.data.model.TextParams
import xyz.aiinirii.imarkdownx.entity.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "MyViewModel"

class MyViewModel : ViewModel() {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT)
    private val simple2DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ROOT)
    private val sharedPreferences =
        context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
    private val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)
    private val fileRepository = FileRepository(DatabaseRepository.fileDao())
    private val folderRepository = FolderRepository(DatabaseRepository.folderDao())
    private val remoteFileRepository = RemoteFileRepository(RetrofitRepository.fileApi)

    val isGuest = MutableLiveData<Boolean>().apply {
        val userLocalName = sharedPreferences.getString("userLocalName", "")
        if (userLocalName.equals("Guest")) {
            postValue(true)
        } else {
            postValue(false)
        }
    }

    val loginStart = MutableLiveData<Boolean>()
    val registerStart = MutableLiveData<Boolean>()
    val updateInfoStart = MutableLiveData<Boolean>()
    val changePasswordStart = MutableLiveData<Boolean>()
    val deleteStart = MutableLiveData<Boolean>()

    val isSyncing = MutableLiveData<Boolean>()

    val usernameInitial = MutableLiveData<String>()
    val usernameWithOutInitial = MutableLiveData<String>()
    val notesCountText = MutableLiveData<String>()

    init {
        countNotes()
    }

    fun sync() {
        GlobalScope.launch(Dispatchers.IO) {
            val token = sharedPreferences.getString("token", "")

            isSyncing.postValue(true)
            // load all files
            remoteFileRepository.getAllFiles(
                token = token!!,
                object : Callback<CommonResult<List<TextFullInfoParams>>> {
                    override fun onResponse(
                        call: Call<CommonResult<List<TextFullInfoParams>>>,
                        response: Response<CommonResult<List<TextFullInfoParams>>>
                    ) {
                        if (response.body() != null) {
                            val body = response.body()!!
                            val downloadingFiles = AtomicInteger(0)
                            body.data.forEach { textFullInfoParams ->
                                GlobalScope.launch(Dispatchers.IO) {
                                    val fileByRemoteId = fileRepository.getFileByRemoteId(textFullInfoParams.id)
                                    if (fileByRemoteId == null) {
                                        GlobalScope.launch(Dispatchers.IO) {
                                            downloadingFiles.incrementAndGet()
                                            val file = File(
                                                name = textFullInfoParams.title,
                                                date = simpleDateFormat.format(textFullInfoParams.editTime),
                                                content = textFullInfoParams.content,
                                                locked = textFullInfoParams.privacy == 1,
                                                remoteId = textFullInfoParams.id
                                            )
                                            file.folderId = textFullInfoParams.folderId
                                            fileRepository.insert(file)
                                            folderRepository.createFolderIfNotExist(file.folderId)
                                            downloadingFiles.decrementAndGet()
                                        }
                                    }
                                }
                            }
                            Thread.sleep(200)
                            while (downloadingFiles.get() != 0) {
                                Thread.sleep(200)
                            }
                        }
                        GlobalScope.launch(Dispatchers.IO) {
                            val files = fileRepository.getAllFiles()
                            val syncProcess = AtomicInteger(files.size)
                            files.forEach { file: File ->
                                if (file.remoteId == null) {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        remoteFileRepository.createFile(
                                            token,
                                            TextParams(
                                                file.content,
                                                simple2DateFormat.format(simpleDateFormat.parse(file.date)!!),
                                                if (file.locked) 1 else 0,
                                                file.name,
                                                file.folderId
                                            ),
                                            object : Callback<CommonResult<TextFullInfoParams>> {
                                                override fun onResponse(
                                                    call: Call<CommonResult<TextFullInfoParams>>,
                                                    response: Response<CommonResult<TextFullInfoParams>>
                                                ) {
                                                    GlobalScope.launch(Dispatchers.IO) {
                                                        file.remoteId = response.body()!!.data.id
                                                        fileRepository.update(file)
                                                        syncProcess.decrementAndGet()
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<CommonResult<TextFullInfoParams>>,
                                                    t: Throwable
                                                ) {
                                                    Log.e(TAG, "onFailure: ${t.message}")
                                                }
                                            }
                                        )
                                    }
                                } else {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        remoteFileRepository.updateFile(
                                            token,
                                            file.remoteId!!,
                                            TextParams(
                                                file.content,
                                                simple2DateFormat.format(simpleDateFormat.parse(file.date)!!),
                                                if (file.locked) 1 else 0,
                                                file.name,
                                                file.folderId
                                            ),
                                            object : Callback<CommonResult<String>> {
                                                override fun onResponse(
                                                    call: Call<CommonResult<String>>,
                                                    response: Response<CommonResult<String>>
                                                ) {
                                                    syncProcess.decrementAndGet()
                                                }

                                                override fun onFailure(
                                                    call: Call<CommonResult<String>>,
                                                    t: Throwable
                                                ) {
                                                    Log.e(TAG, "onFailure: ${t.message}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            while (syncProcess.get() != 0) {
                                Thread.sleep(200)
                            }
                            isSyncing.postValue(false)
                        }
                    }

                    override fun onFailure(call: Call<CommonResult<List<TextFullInfoParams>>>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                }
            )
        }
    }

    fun updateInfo() {
        updateInfoStart.postValue(true)
    }

    fun countNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val countNumber = fileRepository.count()
            notesCountText.postValue(
                context.getString(R.string.count_notes_text).format(countNumber)
            )
        }
    }

    fun changePassword() {
        changePasswordStart.postValue(true)
    }

    fun logout() {
        reloadGuest()
    }

    fun delete() {
        deleteStart.postValue(true)
    }

    private fun reloadGuest() {
        setUsername(context.getString(R.string.default_username))
        sharedPreferences.edit()
            .putString("userLocalName", context.getString(R.string.default_username))
            .remove("token")
            .apply()
    }

    fun setUsername(username: String) {
        if (username != context.getString(R.string.default_username)) {
            isGuest.postValue(false)
        } else {
            isGuest.postValue(true)
        }
        usernameInitial.postValue(username[0].toString().capitalize(Locale.ROOT))
        usernameWithOutInitial.postValue(username.subSequence(1, username.length).toString())
    }

    fun login() {
        loginStart.postValue(true)
    }

    fun register() {
        registerStart.postValue(true)
    }

    fun deleteUser() {
        val token = sharedPreferences.getString("token", "")
        remoteUserRepository.delete(token!!, object : Callback<CommonResult<String>> {
            override fun onResponse(
                call: Call<CommonResult<String>>,
                response: Response<CommonResult<String>>
            ) {
                if (response.body()?.code == 200) {
                    reloadGuest()
                }
                Toast.makeText(
                    context,
                    response.body()?.message,
                    Context.MODE_PRIVATE
                ).show()
            }

            override fun onFailure(call: Call<CommonResult<String>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}