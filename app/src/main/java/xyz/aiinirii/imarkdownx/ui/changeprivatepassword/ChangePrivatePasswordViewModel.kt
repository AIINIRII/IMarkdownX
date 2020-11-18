package xyz.aiinirii.imarkdownx.ui.changeprivatepassword

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.data.UserRepository
import xyz.aiinirii.imarkdownx.db.AppDatabase

private const val TAG = "ChangePrivatePasswordVi"

class ChangePrivatePasswordViewModel : ViewModel() {

    private val userRepository: UserRepository
    private val sharedPreferences: SharedPreferences

    init {
        val userDao = AppDatabase.getDatabase(IMarkdownXApplication.context).userDao()
        userRepository = UserRepository(userDao)
        sharedPreferences = IMarkdownXApplication.context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
    }

    val originPassword = MutableLiveData<String>()

    val newPassword = MutableLiveData<String>()

    val newConfirmPassword = MutableLiveData<String>()

    private val _isChangingSuccess = MutableLiveData<Boolean>()
    val isChangingSuccess: LiveData<Boolean>
        get() = _isChangingSuccess

    private val _isCancel = MutableLiveData<Boolean>()
    val isCancel: LiveData<Boolean>
        get() = _isCancel

    private val _isConfirm = MutableLiveData<Boolean>()
    val isConfirm: LiveData<Boolean>
        get() = _isConfirm

    fun confirm() {
        _isConfirm.postValue(true)
    }

    fun cancel() {
        _isCancel.postValue(true)
    }

    fun changePassword() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentOriginPassword = originPassword.value
            val currentNewPassword = newPassword.value

            val userId = sharedPreferences.getLong("userLocalId", -1L)
            if (userId != -1L) {
                if (userRepository.verifyPrivatePassword(userId, currentOriginPassword!!)) {
                    val user = userRepository.get(userId)
                    if (user != null) {
                        userRepository.savePrivatePassword(user, currentNewPassword!!)
                        _isChangingSuccess.postValue(true)
                        Log.i(TAG, "changePassword: correct password")
                        return@launch
                    }
                }
            } else {
                Log.e(TAG, "changePassword: can not find user id")
            }
            Log.i(TAG, "changePassword: wrong password")
            _isChangingSuccess.postValue(false)
        }
    }

    fun verifyConfirmPassword() = newPassword.value == newConfirmPassword.value
}