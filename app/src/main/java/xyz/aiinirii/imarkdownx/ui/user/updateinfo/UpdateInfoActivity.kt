package xyz.aiinirii.imarkdownx.ui.user.updateinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.aiinirii.imarkdownx.R

class UpdateInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UpdateInfoFragment.newInstance())
                .commitNow()
        }
    }
}