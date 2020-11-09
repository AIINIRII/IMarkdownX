package xyz.aiinirii.imarkdownx.helper

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*


/**
 *
 * @author AIINIRII
 */
class EditTextManager(private val editText: EditText) : TextWatcher {
    private var editTextOperation: EditTextOperationImpl? = null
    private val undoOperations: LinkedList<EditTextOperationImpl> = LinkedList()
    private val redoOperations: LinkedList<EditTextOperationImpl> = LinkedList()

    private var enable = true

    fun disable(): EditTextManager? {
        enable = false
        return this
    }

    fun enable(): EditTextManager? {
        enable = true
        return this
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (count > 0) {
            val end = start + count
            if (enable) {
                if (editTextOperation == null) {
                    editTextOperation = EditTextOperationImpl()
                }
                //记录原始内容
                editTextOperation!!.setSrc(s.subSequence(start, end), start, end)
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (count > 0) {
            val end = start + count
            if (enable) {
                if (editTextOperation == null) {
                    editTextOperation = EditTextOperationImpl()
                }
                //记录目标内容
                editTextOperation!!.setDst(s.subSequence(start, end), start, end)
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (enable && editTextOperation != null) {
            if (!redoOperations.isEmpty()) {
                redoOperations.clear()
            }
            undoOperations.push(editTextOperation)
        }
        editTextOperation = null
    }

    /**
     *
     * @param preload whether there is a preload oper
     * @return Boolean
     */
    fun canUndo(preload: Boolean): Boolean = if (preload) undoOperations.size > 1 else !undoOperations.isEmpty()

    /**
     *
     * @param preload whether there is a preload oper
     * @return Boolean
     */
    fun canRedo(preload: Boolean): Boolean = if (preload) redoOperations.size > 1 else !redoOperations.isEmpty()

    /**
     *
     * @param preload whether there is a preload oper
     * @return Boolean
     */
    fun undo(preload: Boolean): Boolean {
        if (canUndo(preload)) {
            val undoOpt: EditTextOperationImpl = undoOperations.pop()

            disable()
            undoOpt.undo(editText)
            enable()

            redoOperations.push(undoOpt)
            return true
        }
        return false
    }

    /**
     *
     * @param preload whether there is a preload oper
     * @return Boolean
     */
    fun redo(preload: Boolean): Boolean {
        if (canRedo(preload)) {
            val redoOpt: EditTextOperationImpl = redoOperations.pop()

            disable()
            redoOpt.redo(editText)
            enable()

            undoOperations.push(redoOpt)
            return true
        }
        return false
    }

    private val undoMessage = "UNDO_MESSAGE"
    private val redoMessage = "REDO_MESSAGE"

    fun exportState(): Bundle? {
        val state = Bundle()
        state.putSerializable(undoMessage, undoOperations)
        state.putSerializable(redoMessage, redoOperations)
        return state
    }

    fun importState(state: Bundle) {
        val savedUndoOpts: Collection<EditTextOperationImpl>? =
            state.getSerializable(undoMessage) as Collection<EditTextOperationImpl>?
        undoOperations.clear()
        if (savedUndoOpts != null) {
            undoOperations.addAll(savedUndoOpts)
        }
        val savedRedoOpts: Collection<EditTextOperationImpl>? =
            state.getSerializable(redoMessage) as Collection<EditTextOperationImpl>?
        redoOperations.clear()
        if (savedRedoOpts != null) {
            redoOperations.addAll(savedRedoOpts)
        }
    }

}