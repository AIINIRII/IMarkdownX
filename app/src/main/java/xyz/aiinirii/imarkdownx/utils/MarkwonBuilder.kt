package xyz.aiinirii.imarkdownx.utils

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spanned
import android.util.Log
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListDrawable
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.data.DataUriSchemeHandler
import io.noties.markwon.image.file.FileSchemeHandler
import io.noties.markwon.image.network.NetworkSchemeHandler
import xyz.aiinirii.imarkdownx.IMarkdownXApplication.Companion.context
import xyz.aiinirii.imarkdownx.R


private const val TAG = "MarkwonBuilder"

object MarkwonBuilder {
    var drawable = TaskListDrawable(Color.GRAY, Color.GRAY, Color.WHITE)
    val markwon = Markwon.builder(context)
        .usePlugin(ImagesPlugin.create())
        .usePlugin(ImagesPlugin.create { plugin ->
            plugin.addSchemeHandler(FileSchemeHandler.createWithAssets(context))
            plugin.addSchemeHandler(DataUriSchemeHandler.create())
            plugin.addSchemeHandler(NetworkSchemeHandler.create())
            plugin.errorHandler { url, throwable ->
                Log.e(TAG, "MarkwonBuilder: error!! url:${url}, throwable:${throwable.message}")
                AppCompatResources.getDrawable(context, R.drawable.ic_error)
            }
            plugin.placeholderProvider {
                AppCompatResources.getDrawable(context, R.drawable.ic_loading)
            }
        })
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                builder.linkResolver { _, link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                }
            }

            override fun beforeSetText(textView: TextView, markdown: Spanned) {
                AsyncDrawableScheduler.unschedule(textView)
            }

            override fun afterSetText(textView: TextView) {
                AsyncDrawableScheduler.schedule(textView)
            }
        })
        .usePlugin(HtmlPlugin.create())
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(
            TaskListPlugin.create(
                context.getColor(R.color.colorPrimary),
                context.getColor(R.color.colorPrimary),
                context.getColor(R.color.colorPrimaryDark)
            )
        )
        .build()
}