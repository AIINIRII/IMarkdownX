package xyz.aiinirii.imarkdownx.ui.user.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.aiinirii.imarkdownx.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }
}