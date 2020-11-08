package xyz.aiinirii.imarkdownx.ui.edit.render

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.noties.markwon.Markwon

class EditRenderViewModel : ViewModel() {

    private val _renderedText = MutableLiveData<Spanned>()
    val renderText: LiveData<Spanned>
        get() = _renderedText

    private val _isBack = MutableLiveData<Boolean>()
    val isBack: LiveData<Boolean>
        get() = _isBack

    val fileTitle = MutableLiveData<String>()

    fun renderText(markwon: Markwon, content: String) {
        _renderedText.postValue(markwon.toMarkdown(content))
    }

    fun back() {
        _isBack.postValue(false)
    }
}