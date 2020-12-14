package xyz.aiinirii.imarkdownx.data.model

data class UserInfoParams(
    val createTime: String,
    val email: String,
    val gender: Int,
    val id: Long,
    val nickname: String,
    val password: String?,
    val phone: String,
    val status: Int,
    val username: String
)