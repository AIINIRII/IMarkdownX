package xyz.aiinirii.imarkdownx.ui.edit.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.R

class EditMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_edit_main, EditMainFragment.newInstance())
                .commitNow()
        }
    }

    fun obtainViewModel(): EditMainViewModel? = ViewModelProvider(this).get(EditMainViewModel::class.java)
}