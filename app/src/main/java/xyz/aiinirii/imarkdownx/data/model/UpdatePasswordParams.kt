package xyz.aiinirii.imarkdownx.data.model

data class UpdatePasswordParams(
    val newPassword: String,
    val oldPassword: String,
    val username: String
)