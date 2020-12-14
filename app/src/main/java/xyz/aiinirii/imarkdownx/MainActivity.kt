package xyz.aiinirii.imarkdownx

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.aiinirii.imarkdownx.data.RemoteUserRepository
import xyz.aiinirii.imarkdownx.data.RetrofitRepository
import xyz.aiinirii.imarkdownx.data.dao.UserDao
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.data.model.TokenParams
import xyz.aiinirii.imarkdownx.db.AppDatabase
import xyz.aiinirii.imarkdownx.entity.User
import xyz.aiinirii.imarkdownx.utils.MD5Utils

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
            lifecycleScope.launch(Dispatchers.IO) {
                addGuestAsUser(userDao, sharedPreferences)
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val user = userDao.findUserById(userLocalId)
                if (user == null) {
                    addGuestAsUser(userDao, sharedPreferences)
                }
            }
        }
        val remoteUserRepository = RemoteUserRepository(RetrofitRepository.userApi)
        val token = sharedPreferences.getString("token", "")
        if (token != null && token != "") {
            GlobalScope.launch(Dispatchers.IO) {
                remoteUserRepository.refreshToken(token, object : Callback<CommonResult<TokenParams>> {
                    override fun onResponse(
                        call: Call<CommonResult<TokenParams>>,
                        response: Response<CommonResult<TokenParams>>
                    ) {
                        if (response.body()?.code == 200) {
                            val newToken = response.body()!!.data.let {
                                it.tokenHead + it.token
                            }
                            sharedPreferences.edit()
                                .putString("token", newToken)
                                .apply()
                        } else {
                            sharedPreferences.edit()
                                .remove("token")
                                .putString("userLocalName", getString(R.string.default_username))
                                .apply()
                        }

                    }

                    override fun onFailure(call: Call<CommonResult<TokenParams>>, t: Throwable) {
                        Toast.makeText(
                            IMarkdownXApplication.context,
                            getString(R.string.server_error_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedPreferences.edit()
                            .remove("token")
                            .putString("userLocalName", getString(R.string.default_username))
                            .apply()
                    }
                })
            }
        }
    }

    private suspend fun addGuestAsUser(
        userDao: UserDao,
        sharedPreferences: SharedPreferences
    ) {
        val newUserId = userDao.insertUser(User(getString(R.string.default_username), MD5Utils.getMD5Code("")))
        sharedPreferences.edit()
            .putLong("userLocalId", newUserId)
            .putString("userLocalName", "Guest")
            .commit()
    }

}