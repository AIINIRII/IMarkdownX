package xyz.aiinirii.imarkdownx.data.api

import retrofit2.Call
import retrofit2.http.*
import xyz.aiinirii.imarkdownx.config.ParamsConfig.tokenHeader
import xyz.aiinirii.imarkdownx.data.model.*

/**
 * Contains all the apis about sso
 * @author AIINIRII
 */
interface UserApi {

    /**
     * login api
     *
     * @param userLoginParam UserLoginParam
     * @return Call<CommonResult<TokenMapParam>>
     */
    @POST("/sso/login")
    fun login(
        @Body userLoginParam: UserLoginParams
    ): Call<CommonResult<TokenParams>>

    /**
     * register api
     *
     * @param userRegisterParam UserRegisterParam
     * @return Call<CommonResult<UserRegisterReturnParam>>
     */
    @POST("/sso/register")
    fun register(
        @Body userRegisterParam: UserUpdateParams
    ): Call<CommonResult<UserInfoParams>>

    /**
     * delete api
     *
     * @param token String
     * @return Call<CommonResult<Unit>>
     */
    @POST("/sso/delete")
    fun delete(
        @Header(tokenHeader) token: String
    ): Call<CommonResult<String>>

    /**
     * query user info api
     *
     * @param token String
     * @return Call<CommonResult<UserRegisterReturnParam>>
     */
    @GET("/sso/info")
    fun userInfo(
        @Header(tokenHeader) token: String
    ): Call<CommonResult<UserInfoParams>>

    /**
     * refresh token api
     *
     * @param token String
     * @return Call<CommonResult<TokenMapParam>>
     */
    @POST("/sso/refreshToken")
    fun refreshToken(
        @Header(tokenHeader) token: String
    ): Call<CommonResult<TokenParams>>

    /**
     * update user info api
     *
     * @param token String
     * @param userRegisterParam UserRegisterParam
     * @param id Long
     * @return Call<CommonResult<Unit>>
     */
    @POST("/sso/update/{id}")
    fun updateInfo(
        @Header(tokenHeader) token: String,
        @Body userRegisterParam: UserUpdateParams,
        @Path("id") id: Long
    ): Call<CommonResult<String>>

    /**
     * update password api
     *
     * @param memberPasswordParam MemberPasswordParam
     * @return Call<CommonResult<Unit>>
     */
    @POST("/sso/updatePassword")
    fun updatePassword(
        @Body memberPasswordParam: UpdatePasswordParams
    ): Call<CommonResult<String>>
}