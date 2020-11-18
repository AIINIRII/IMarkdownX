package xyz.aiinirii.imarkdownx.data

import android.util.Log
import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.UserDao
import xyz.aiinirii.imarkdownx.entity.User
import xyz.aiinirii.imarkdownx.utils.MD5Utils

/**
 *
 * @author AIINIRII
 */

private const val TAG = "UserRepository"

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User): Long {
        Log.d(TAG, "insert: insert user")
        return userDao.insertUser(user)
    }

    suspend fun update(user: User) {
        Log.d(TAG, "update: begin to update the user")
        userDao.updateUser(user)
    }

    suspend fun get(userId: Long): User? = userDao.findUserById(userId)

    suspend fun delete(deleteUser: User) {
        userDao.deleteUser(deleteUser)
    }

   suspend fun verifyPrivatePassword(userId: Long, privatePassword: String): Boolean {
        val user = get(userId)
        return user?.let {  MD5Utils.verifyMd5Code(privatePassword, it.privatePassword)}?:false
    }

   suspend fun havePrivatePassword(userId: Long):Boolean {
        val user = get(userId)
        return user?.privatePassword != ""
    }

    suspend fun savePrivatePassword(user: User, privatePassword: String){
        Log.d(TAG, "savePrivatePassword: begin to save password")
        val md5Password = MD5Utils.getMD5Code(privatePassword)
        user.privatePassword = md5Password
        update(user)
    }
}