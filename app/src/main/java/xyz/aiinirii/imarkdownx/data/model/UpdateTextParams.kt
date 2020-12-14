package xyz.aiinirii.imarkdownx.data.model

data class UpdateTextParams(
    val content: String,
    val editTime: String,
    val privacy: Int,
    val title: String
)