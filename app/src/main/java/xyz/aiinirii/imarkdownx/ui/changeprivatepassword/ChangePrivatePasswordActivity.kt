package xyz.aiinirii.imarkdownx.ui.changeprivatepassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.aiinirii.imarkdownx.R

class ChangePrivatePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_private_password)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChangePrivatePasswordFragment.newInstance())
                .commitNow()
        }
    }
}