package xyz.aiinirii.imarkdownx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        val x = Handler()
        background_image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.launch_anim))
        x.postDelayed({
                val intent = Intent(IMarkdownXApplication.context, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
    }
}