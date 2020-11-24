package xyz.aiinirii.imarkdownx.helper

import android.os.Parcel
import android.os.Parcelable.Creator
import android.widget.EditText


/**
 *
 * @author AIINIRII
 */
class EditTextOperationImpl constructor() : EditTextOperation {
    private var src: String? = null
    private var srcStart = 0
    private var srcEnd = 0

    private var dst: String? = null
    private var dstStart = 0
    private var dstEnd = 0

    lateinit var editText: EditText

    constructor(editText: EditText) : this() {
        this.editText = editText
    }

    constructor(`in`: Parcel) : this() {
        this.src = `in`.readString()
        this.srcStart = `in`.readInt()
        this.srcEnd = `in`.readInt()
        this.dst = `in`.readString()
        this.dstStart = `in`.readInt()
        this.dstEnd = `in`.readInt()
    }

    fun setSrc(src: CharSequence?, srcStart: Int, srcEnd: Int) {
        this.src = src?.toString() ?: ""
        this.srcStart = srcStart
        this.srcEnd = srcEnd
    }

    fun setDst(dst: CharSequence?, dstStart: Int, dstEnd: Int) {
        this.dst = dst?.toString() ?: ""
        this.dstStart = dstStart
        this.dstEnd = dstEnd
    }

    override fun undo(editText: EditText) {
        val editable = editText.text
        var idx = -1
        if (dstEnd > 0) { //删除目标内容
            editable.delete(dstStart, dstEnd)
            if (src == null) {
                idx = dstStart
            }
        }
        if (src != null) { //插入原始内容
            editable.insert(srcStart, src)
            idx = srcStart + src!!.length
        }
        if (idx >= 0) { //恢复光标位置
            editText.setSelection(idx)
        }
    }

    override fun redo(editText: EditText) {
        val editable = editText.text
        var idx = -1
        if (srcEnd > 0) {
            editable.delete(srcStart, srcEnd)
            if (dst == null) {
                idx = srcStart
            }
        }
        if (dst != null) {
            editable.insert(dstStart, dst)
            idx = dstStart + dst!!.length
        }
        if (idx >= 0) {
            editText.setSelection(idx)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(src)
        dest.writeInt(srcStart)
        dest.writeInt(srcEnd)
        dest.writeString(dst)
        dest.writeInt(dstStart)
        dest.writeInt(dstEnd)
    }


    @JvmField
    val CREATOR: Creator<EditTextOperationImpl?> = object : Creator<EditTextOperationImpl?> {
        override fun createFromParcel(source: Parcel): EditTextOperationImpl? {
            return EditTextOperationImpl(source)
        }

        override fun newArray(size: Int): Array<EditTextOperationImpl?> {
            return arrayOfNulls<EditTextOperationImpl>(size)
        }
    }
}