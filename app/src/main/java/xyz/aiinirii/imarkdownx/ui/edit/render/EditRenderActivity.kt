package xyz.aiinirii.imarkdownx.ui.edit.render

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.R

class EditRenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_render)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_edit_render, EditRenderFragment.newInstance())
                .commitNow()
        }
    }

    fun obtainViewModel(): EditRenderViewModel? = ViewModelProvider(this).get(EditRenderViewModel::class.java)
}