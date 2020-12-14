package xyz.aiinirii.imarkdownx.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.aiinirii.imarkdownx.data.api.UserApi
import xyz.aiinirii.imarkdownx.data.model.*

private const val TAG = "RemoteUserRepository"

/**
 *
 * @author AIINIRII
 */
class RemoteUserRepository(private val userApi: UserApi) {

    fun login(userLoginParams: UserLoginParams, callback: Callback<CommonResult<TokenParams>>) {
        userApi.login(userLoginParams).enqueue(callback)
    }

    fun register(userUpdateParams: UserUpdateParams, callback: Callback<CommonResult<UserInfoParams>>) {
        userApi.register(userUpdateParams).enqueue(callback)
    }

    fun updatePassword(passwordParams: UpdatePasswordParams, callback: Callback<CommonResult<String>>) {
        userApi.updatePassword(passwordParams).enqueue(callback)
    }

    fun userInfo(token: String, callback: Callback<CommonResult<UserInfoParams>>) {
        userApi.userInfo(token).enqueue(callback)
    }

    fun delete(token: String, callback: Callback<CommonResult<String>>) {
        userApi.delete(token).enqueue(callback)
    }

    fun refreshToken(token: String, callback: Callback<CommonResult<TokenParams>>) {
        userApi.refreshToken(token).enqueue(callback)
    }

    fun updateInfo(
        token: String,
        userUpdateParams: UserUpdateParams,
        callback: Callback<CommonResult<String>>
    ) {
        userApi.userInfo(token).enqueue(object : Callback<CommonResult<UserInfoParams>?> {
            override fun onResponse(
                call: Call<CommonResult<UserInfoParams>?>,
                response: Response<CommonResult<UserInfoParams>?>
            ) {
                response.body()?.apply {
                    userApi.updateInfo(token, userUpdateParams, this.data.id).enqueue(callback)
                }
            }

            override fun onFailure(call: Call<CommonResult<UserInfoParams>?>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}