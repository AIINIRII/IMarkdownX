package xyz.aiinirii.imarkdownx

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.User

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        // load local user
        val sharedPreferences = getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
        val userLocalId = sharedPreferences.getLong("userLocalId", -1)
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        if (userLocalId == -1L) {
            lifecycleScope.launch {
                val newUserId = userDao.insertUser(User(getString(R.string.default_username), ""))
                sharedPreferences.edit()
                    .putLong("userLocalId", newUserId)
                    .putString("userLocalName", "Guest")
                    .commit()
            }
        }
    }

}