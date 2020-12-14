package xyz.aiinirii.imarkdownx.ui.user.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.aiinirii.imarkdownx.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterFragment.newInstance())
                .commitNow()
        }
    }
}