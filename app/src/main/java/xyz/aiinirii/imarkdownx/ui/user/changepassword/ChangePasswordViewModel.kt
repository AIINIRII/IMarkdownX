package xyz.aiinirii.imarkdownx.ui.user.changepassword

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.data.RemoteUserRepository
import xyz.aiinirii.imarkdownx.data.RetrofitRepository
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.data.model.UpdatePasswordParams

private const val TAG = "ChangePasswordViewModel"

class ChangePasswordViewModel : ViewModel() {
    private val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)

    val username = MutableLiveData<String>()
    val originPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    val endChange = MutableLiveData<Boolean>()
    val startCancel = MutableLiveData<Boolean>()

    fun confirm() {
        val currentUsername = username.value
        val currentOriginPassword = originPassword.value
        val currentNewPassword = newPassword.value
        val currentConfirmPassword = confirmPassword.value

        if (currentOriginPassword == null || currentOriginPassword == "" ||
            currentNewPassword == null || currentNewPassword == "" ||
            currentUsername == null || currentUsername == "" ||
            currentConfirmPassword == null || currentConfirmPassword == ""
        ) {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.field_empty_toast),
                Context.MODE_PRIVATE
            ).show()
        } else if (currentConfirmPassword != currentNewPassword) {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.confirm_password_wrong_toast),
                Context.MODE_PRIVATE
            ).show()
        } else {
            val passwordUpdateParams = UpdatePasswordParams(
                oldPassword = currentOriginPassword,
                newPassword = currentNewPassword,
                username = currentUsername
            )
            remoteUserRepository.updatePassword(
                passwordParams = passwordUpdateParams,
                object : Callback<CommonResult<String>> {
                    override fun onResponse(
                        call: Call<CommonResult<String>>,
                        response: Response<CommonResult<String>>
                    ) {
                        Toast.makeText(
                            IMarkdownXApplication.context,
                            response.body()?.message,
                            Context.MODE_PRIVATE
                        ).show()
                        if (response.body()?.code == 200) {
                            endChange.postValue(true)
                        }
                    }

                    override fun onFailure(call: Call<CommonResult<String>>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                }
            )
        }
    }

    fun cancel() {
        startCancel.postValue(true)
    }

}