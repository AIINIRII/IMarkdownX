package xyz.aiinirii.imarkdownx.ui.user.register

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
import xyz.aiinirii.imarkdownx.data.model.UserInfoParams
import xyz.aiinirii.imarkdownx.data.model.UserUpdateParams

private const val TAG = "RegisterViewModel"

class RegisterViewModel : ViewModel() {
    private val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val gender = MutableLiveData<Int>()

    val endRegister = MutableLiveData<Boolean>()
    val startCancel = MutableLiveData<Boolean>()

    fun confirm() {
        val currentUsername = username.value
        val currentPassword = password.value
        val currentConfirmPassword = confirmPassword.value
        val currentNickname = nickname.value
        val currentEmail = email.value
        val currentPhone = phone.value
        val currentGender = gender.value

        if (currentUsername == null || currentUsername == "" ||
            currentPassword == null || currentPassword == "" ||
            currentConfirmPassword == null || currentConfirmPassword == "" ||
            currentNickname == null || currentNickname == "" ||
            currentEmail == null || currentEmail == "" ||
            currentPhone == null || currentPhone == ""
        ) {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.field_empty_toast),
                Context.MODE_PRIVATE
            ).show()
        } else if (currentConfirmPassword != currentPassword) {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.confirm_password_wrong_toast),
                Context.MODE_PRIVATE
            ).show()
        } else {
            val gender = when (currentGender) {
                R.id.male -> 0
                R.id.female -> 1
                else -> 0
            }
            val userUpdateParams = UserUpdateParams(
                username = currentUsername,
                password = currentPassword,
                email = currentEmail,
                gender = gender,
                nickName = currentNickname,
                phone = currentPhone
            )
            remoteUserRepository.register(
                userUpdateParams = userUpdateParams,
                object : Callback<CommonResult<UserInfoParams>> {
                    override fun onResponse(
                        call: Call<CommonResult<UserInfoParams>>,
                        response: Response<CommonResult<UserInfoParams>>
                    ) {
                        Toast.makeText(
                            IMarkdownXApplication.context,
                            response.body()?.message,
                            Context.MODE_PRIVATE
                        ).show()
                        if (response.body()?.code == 200) {
                            endRegister.postValue(true)
                        }
                    }

                    override fun onFailure(call: Call<CommonResult<UserInfoParams>>, t: Throwable) {
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