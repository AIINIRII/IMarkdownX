package xyz.aiinirii.imarkdownx.ui.user.login

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
import xyz.aiinirii.imarkdownx.data.model.TokenParams
import xyz.aiinirii.imarkdownx.data.model.UserLoginParams

private const val TAG = "LoginViewModel"

class LoginViewModel : ViewModel() {

    private val sharedPreferences =
        IMarkdownXApplication.context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
    private val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val startRegister = MutableLiveData<Boolean>()
    val endLogin = MutableLiveData<Boolean>()
    var loginSuccess = false

    fun login() {
        val currentUsername = username.value
        val currentPassword = password.value

        if (currentUsername == null || currentUsername == "") {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.username_empty_toast),
                Toast.LENGTH_SHORT
            ).show()
        } else if (currentPassword == null || currentPassword == "") {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.password_empty_toast),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            remoteUserRepository.login(
                UserLoginParams(
                    username = currentUsername,
                    password = currentPassword
                ),
                object : Callback<CommonResult<TokenParams>> {
                    override fun onResponse(
                        call: Call<CommonResult<TokenParams>>,
                        response: Response<CommonResult<TokenParams>>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.code == 200) {
                                val edit = sharedPreferences.edit()
                                edit.putString("token", response.body()!!.data.tokenHead + response.body()!!.data.token)
                                edit.putString("userLocalName", currentUsername)
                                edit.apply()
                                loginSuccess = true
                                endLogin.postValue(true)
                            }
                            Toast.makeText(
                                IMarkdownXApplication.context,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CommonResult<TokenParams>>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                }
            )
        }
    }

    fun register() {
        startRegister.postValue(true)
    }
}