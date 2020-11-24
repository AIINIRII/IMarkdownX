package xyz.aiinirii.imarkdownx.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import xyz.aiinirii.imarkdownx.helper.EditTextManager

private const val TAG = "StateEditText"

/**
 *
 * @author AIINIRII
 */
class StateEditText : AppCompatEditText, MarkdownEditor {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr)

    private val editTextManager: EditTextManager = EditTextManager(this)

    init {
        addTextChangedListener(editTextManager)
    }

    private val keySuper = "KEY_SUPER"
    private val keyOperation: String = EditTextManager::class.java.canonicalName!!

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(keySuper, super.onSaveInstanceState())
        bundle.putBundle(keyOperation, editTextManager.exportState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        val superState = bundle.getParcelable<Parcelable>(keySuper)
        editTextManager.disable()
        super.onRestoreInstanceState(superState)
        editTextManager.enable()
        editTextManager.importState(bundle.getBundle(keyOperation)!!)
    }

    fun canUndo(preload: Boolean): Boolean {
        return editTextManager.canUndo(preload)
    }

    fun canRedo(preload: Boolean): Boolean {
        return editTextManager.canRedo(preload)
    }

    override fun undo(preload: Boolean): Boolean {
        return editTextManager.undo(preload)
    }

    override fun redo(preload: Boolean): Boolean {
        return editTextManager.redo(preload)
    }

    override fun delete(): Boolean {
        editableText.insert(selectionStart, "~~")
        editableText.insert(selectionEnd, "~~")
        setSelection(selectionEnd - 2)
        return true
    }

    override fun heading(): Boolean {
        val beginOfLinePos = beginOfLinePos()
        if (editableText.length > beginOfLinePos + 1 && editableText[beginOfLinePos + 1] == '#') {
            var count = 1
            while (editableText[beginOfLinePos + 1 + count] == '#') {
                count++
            }
            if (count == 6) {
                editableText.delete(beginOfLinePos + 1, beginOfLinePos + 8)
            } else {
                editableText.insert(beginOfLinePos + count, "#")
            }
        } else {
            editableText.insert(beginOfLinePos + 1, "# ")
        }
        return true
    }

    override fun bold(): Boolean {
        editableText.insert(selectionStart, "**")
        editableText.insert(selectionEnd, "**")
        setSelection(selectionEnd - 2)
        return true
    }

    override fun link(): Boolean {
        editableText.insert(selectionStart, "[")
        editableText.insert(selectionEnd, "]()")
        setSelection(selectionEnd - 1)
        return true
    }

    override fun italic(): Boolean {
        editableText.insert(selectionStart, "*")
        editableText.insert(selectionEnd, "*")
        setSelection(selectionEnd - 1)
        return true
    }

    override fun hr(): Boolean {
        editableText.delete(selectionStart, selectionEnd)
        editableText.insert(selectionStart, "\n---\n")
        return true
    }

    override fun code(): Boolean {
        val st = selectionStart
        editableText.insert(selectionStart, "```\n")
        editableText.insert(selectionEnd, "\n```")
        setSelection(st + 3)
        return true
    }

    override fun quote(): Boolean {
        var count = 0
        if (selectionStart == selectionEnd) {
            editableText.insert(beginOfLinePos() + 1, "> ")
        } else {
            val st = selectionStart
            val en = selectionEnd + 1
            editableText.insert(beginOfLinePos() + 1, "> ")
            editableText.subSequence(st, en).forEachIndexed { index: Int, c: Char ->
                if (c == '\n') {
                    val pos = st + 1 + index + count
                    editableText.insert(pos, "> ")
                    count += 2
                }
            }
        }
        return true
    }

    override fun linkAlt(): Boolean {
        var count = 0
        if (selectionStart == selectionEnd) {
            editableText.insert(beginOfLinePos() + 1, "- [ ] ")
        } else {
            val st = selectionStart
            val en = selectionEnd + 5
            editableText.insert(beginOfLinePos() + 1, "- [ ] ")
            editableText.subSequence(st, en).forEachIndexed { index: Int, c: Char ->
                if (c == '\n') {
                    val pos = st + 1 + index + count
                    editableText.insert(pos, "- [ ] ")
                    count += 6
                }
            }
        }
        return true
    }

    override fun list(): Boolean {
        var count = 0
        if (selectionStart == selectionEnd) {
            editableText.insert(beginOfLinePos() + 1, "- ")
        } else {
            val st = selectionStart
            val en = selectionEnd + 1
            editableText.insert(beginOfLinePos() + 1, "- ")
            editableText.subSequence(st, en).forEachIndexed { index: Int, c: Char ->
                if (c == '\n') {
                    val pos = st + 1 + index + count
                    editableText.insert(pos, "- ")
                    count += 2
                }
            }
        }
        return true
    }

    override fun table(row: Int, col: Int): Boolean {
        val st = selectionStart
        val stringBuilder = StringBuilder("\n|")
        for (i in 1..col) {
            stringBuilder.append("  |")
        }
        stringBuilder.append("\n|")
        for (i in 1..col) {
            stringBuilder.append("--|")
        }
        for (r in 1..row) {
            stringBuilder.append("\n|")
            for (i in 1..col) {
                stringBuilder.append("  |")
            }
        }
        stringBuilder.append("\n")
        editableText.delete(selectionStart, selectionEnd)
        editableText.insert(selectionStart, stringBuilder.toString())
        setSelection(st + 3)
        return true
    }

    override fun image(url: String): Boolean {
        editableText.insert(selectionStart, "![")
        editableText.insert(selectionEnd, "]($url)")
        setSelection(selectionEnd - 1)
        return true
    }

    private fun beginOfLinePos() = editableText.subSequence(0, selectionStart).indexOfLast { c -> c == '\n' }
}
