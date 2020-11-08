package xyz.aiinirii.imarkdownx

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 *
 * @author AIINIRII
 */
class IMarkdownXApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}