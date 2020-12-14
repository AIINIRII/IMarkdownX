package xyz.aiinirii.imarkdownx.data.api

import retrofit2.Call
import retrofit2.http.*
import xyz.aiinirii.imarkdownx.config.ParamsConfig
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.data.model.TextFullInfoParams
import xyz.aiinirii.imarkdownx.data.model.TextParams

/**
 *
 * @author AIINIRII
 */
interface FileApi {

    @POST("/text/update/{id}")
    fun update(
        @Header(ParamsConfig.tokenHeader) token: String,
        @Path("id") id: Long,
        @Body textParams: TextParams
    ): Call<CommonResult<String>>

    @GET("/text/my")
    fun myText(
        @Header(ParamsConfig.tokenHeader) token: String
    ): Call<CommonResult<List<TextFullInfoParams>>>

    @POST("/text/delete/{id}")
    fun delete(
        @Header(ParamsConfig.tokenHeader) token: String,
        @Path("id") id: Long
    ): Call<CommonResult<String>>

    @POST("/text/create")
    fun create(
        @Header(ParamsConfig.tokenHeader) token: String,
        @Body textParams: TextParams
    ): Call<CommonResult<TextFullInfoParams>>
}