package xyz.aiinirii.imarkdownx.data.api

import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.aiinirii.imarkdownx.data.model.UpdatePasswordParams
import xyz.aiinirii.imarkdownx.data.model.UserLoginParams
import xyz.aiinirii.imarkdownx.data.model.UserUpdateParams
import xyz.aiinirii.mindspark.data.TestAccount

class SsoApiTest {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:8085")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userRemoteService: UserApi = retrofit
        .create(UserApi::class.java)

    @Test
    fun regex() {
        val regex = Regex("(- \\[[x ]]).*(\n)")
        val s = "- [x] addbsdbsdfdsf\naa- [ ] addbsdbsdfdsf\n"
        regex.findAll(s).forEach {
            val trim = it.value.trim()
            println(trim[3])
        }
    }

    @Test
    fun ssoApiTest() {
        var id = 0L
        var token = ""

        // register test
        TestAccount.apply {
            val register = userRemoteService.register(
                UserUpdateParams(
                    username = username,
                    password = password,
                    gender = gender,
                    nickName = nickName,
                    phone = phone,
                    email = email
                )
            )
            println(register.execute().body())
        }

        // login test
        TestAccount.apply {
            val login = userRemoteService.login(
                UserLoginParams(
                    username = username,
                    password = password
                )
            )
            val message = login.execute()
            println(message.body())
            message.body()?.apply {
                token = data.tokenHead + data.token
            }
        }

        // refresh token test
        TestAccount.apply {
            val tokenMapParamObservable = userRemoteService.refreshToken(token)
            val message = tokenMapParamObservable.execute()
            println(message.body())
            message.body()?.apply {
                token = data.tokenHead + data.token
            }
        }

        // get user info test
        TestAccount.apply {
            val getInfoObservable = userRemoteService.userInfo(token)
            val message = getInfoObservable.execute()
            message.body()?.apply {
                id = data.id
                println(data)
            }
        }

        // update user info test
        TestAccount.apply {
            val updateInfoObservable = userRemoteService.updateInfo(
                token,
                UserUpdateParams(
                    username = username,
                    password = password,
                    gender = update_gender,
                    nickName = update_nickName,
                    phone = update_phone,
                    email = update_email
                ),
                id
            )
            updateInfoObservable.execute()

            val login = userRemoteService.login(
                UserLoginParams(
                    username = username,
                    password = password
                )
            )

            val message = login.execute()
            println(message.body())
            message.body()?.apply {
                token = data.tokenHead + data.token
            }


            val getInfoObservable = userRemoteService.userInfo(token)
            val getInfoParams = getInfoObservable.execute()
            println(getInfoParams.body())
        }

        // update password test
        TestAccount.apply {
            val updatePasswordObservable =
                userRemoteService.updatePassword(
                    UpdatePasswordParams(
                        newPassword = update_password,
                        oldPassword = password,
                        username = username
                    )
                )
            println(updatePasswordObservable.execute().body())
        }


        // delete count test
        TestAccount.apply {
            val login = userRemoteService.login(
                UserLoginParams(
                    username = username,
                    password = update_password
                )
            )
            val message = login.execute()
            println(message.body())
            message.body()?.apply {
                token = data.tokenHead + data.token
            }
        }

        TestAccount.apply {
            val deleteObservable = userRemoteService.delete(
                token
            )
            println(deleteObservable.execute().body())
        }
    }
}