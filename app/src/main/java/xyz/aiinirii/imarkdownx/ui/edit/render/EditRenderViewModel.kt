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

    val fileTitle = MutableLiveData<String>()

    fun renderText(markwon: Markwon, content: String) {
        _renderedText.postValue(markwon.toMarkdown(content))
    }
}