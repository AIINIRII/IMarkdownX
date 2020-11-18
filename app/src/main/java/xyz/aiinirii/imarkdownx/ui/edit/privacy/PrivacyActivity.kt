package xyz.aiinirii.imarkdownx.ui.edit.privacy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.aiinirii.imarkdownx.R

class PrivacyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PrivacyFragment.newInstance())
                .commitNow()
        }
    }
}