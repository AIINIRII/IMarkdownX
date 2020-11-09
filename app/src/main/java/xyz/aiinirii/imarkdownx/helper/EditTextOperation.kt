package xyz.aiinirii.imarkdownx.helper

import android.os.Parcelable
import android.widget.EditText
import java.io.Serializable

/**
 *
 * @author AIINIRII
 */
interface EditTextOperation :Serializable,Parcelable{

    /**
     * undo the text edit operation
     * @param editText EditText
     */
    fun undo(editText: EditText)

    /**
     * redo the text edit operation
     * @param editText EditText
     */
    fun redo(editText: EditText)
}