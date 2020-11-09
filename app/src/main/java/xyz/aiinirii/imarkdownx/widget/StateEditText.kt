package xyz.aiinirii.imarkdownx.widget

import android.app.ZygotePreload
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import xyz.aiinirii.imarkdownx.helper.EditTextManager


/**
 *
 * @author AIINIRII
 */
class StateEditText : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr)

    private val editTextManager: EditTextManager = EditTextManager(this)

    fun canUndo(preload: Boolean): Boolean {
        return editTextManager.canUndo(preload)
    }

    fun canRedo(preload: Boolean): Boolean {
        return editTextManager.canRedo(preload)
    }

    fun undo(preload: Boolean): Boolean {
        return editTextManager.undo(preload)
    }

    fun redo(preload: Boolean): Boolean {
        return editTextManager.redo(preload)
    }

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
}