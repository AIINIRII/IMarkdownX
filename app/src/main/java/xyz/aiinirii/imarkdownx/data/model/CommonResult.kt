package xyz.aiinirii.imarkdownx.data.model

data class CommonResult<T>(
    val code: Int,
    val `data`: T,
    val message: String
)