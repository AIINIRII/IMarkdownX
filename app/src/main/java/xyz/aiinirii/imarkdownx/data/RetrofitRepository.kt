package xyz.aiinirii.imarkdownx.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.aiinirii.imarkdownx.config.ParamsConfig.baseUrl
import xyz.aiinirii.imarkdownx.data.api.FileApi
import xyz.aiinirii.imarkdownx.data.api.UserApi

/**
 *
 * @author AIINIRII
 */
object RetrofitRepository {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val fileApi: FileApi = retrofit.create(FileApi::class.java)
}