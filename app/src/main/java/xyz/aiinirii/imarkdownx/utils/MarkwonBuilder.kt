package xyz.aiinirii.imarkdownx.utils

import android.text.Spanned
import android.widget.TextView
import io.noties.markwon.*
import io.noties.markwon.core.MarkwonTheme
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import xyz.aiinirii.imarkdownx.IMarkdownXApplication

object MarkwonBuilder {
    val markwon = Markwon.builder(IMarkdownXApplication.context)
        .build()
}