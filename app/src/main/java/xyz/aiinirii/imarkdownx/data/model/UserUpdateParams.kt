package xyz.aiinirii.imarkdownx.data.model

data class UserUpdateParams(
    val email: String?,
    val gender: Int,
    val nickName: String?,
    val password: String,
    val phone: String?,
    val username: String
)