package xyz.aiinirii.imarkdownx.data

import retrofit2.Callback
import xyz.aiinirii.imarkdownx.data.api.FileApi
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.data.model.TextFullInfoParams
import xyz.aiinirii.imarkdownx.data.model.TextParams

/**
 *
 * @author AIINIRII
 */
class RemoteFileRepository(private val fileApi: FileApi) {
    suspend fun getAllFiles(token: String, callback: Callback<CommonResult<List<TextFullInfoParams>>>, ) {
        fileApi.myText(token).enqueue(callback)
    }

    suspend fun updateFile(token: String, id: Long, file: TextParams, callback: Callback<CommonResult<String>>) {
        fileApi.update(token, id, file).enqueue(callback)
    }

    suspend fun removeFile(token: String, id: Long, callback: Callback<CommonResult<String>>) {
        fileApi.delete(token, id).enqueue(callback)
    }

    suspend fun createFile(token: String, file: TextParams, callback: Callback<CommonResult<TextFullInfoParams>>){
        fileApi.create(token, file).enqueue(callback)
    }
}