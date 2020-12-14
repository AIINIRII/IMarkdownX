package xyz.aiinirii.imarkdownx.data.model

import java.util.*

data class TextFullInfoParams(
    val content: String,
    val editTime: Date,
    val privacy: Int,
    val title: String,
    val id: Long,
    val uid: Long,
    val folderId: Long
)