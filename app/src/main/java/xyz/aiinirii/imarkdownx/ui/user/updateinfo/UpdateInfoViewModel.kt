package xyz.aiinirii.imarkdownx.ui.user.updateinfo

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
import xyz.aiinirii.imarkdownx.data.model.UserUpdateParams

private const val TAG = "UpdateInfoViewModel"

class UpdateInfoViewModel : ViewModel() {
    private val sharedPreferences =
        IMarkdownXApplication.context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
    private val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)

    val nickname = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val gender = MutableLiveData<Int>()

    val endUpdate = MutableLiveData<Boolean>()
    val startCancel = MutableLiveData<Boolean>()

    fun confirm() {
        val currentNickname = nickname.value
        val currentEmail = email.value
        val currentPhone = phone.value
        val currentGender = gender.value

        if (currentNickname == null || currentNickname == "" ||
            currentEmail == null || currentEmail == "" ||
            currentPhone == null || currentPhone == ""
        ) {
            Toast.makeText(
                IMarkdownXApplication.context,
                IMarkdownXApplication.context.getString(R.string.field_empty_toast),
                Context.MODE_PRIVATE
            ).show()
        } else {
            val username = sharedPreferences.getString("userLocalName", "")!!
            val token = sharedPreferences.getString("token", "")!!
            val gender = when (currentGender) {
                R.id.male -> 0
                R.id.female -> 1
                else -> 0
            }
            val userUpdateParams = UserUpdateParams(
                username = username,
                password = "",
                email = currentEmail,
                gender = gender,
                nickName = currentNickname,
                phone = currentPhone
            )
            remoteUserRepository.updateInfo(
                token = token,
                userUpdateParams = userUpdateParams,
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
                            endUpdate.postValue(true)
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