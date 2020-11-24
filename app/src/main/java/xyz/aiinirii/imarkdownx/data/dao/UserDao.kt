package xyz.aiinirii.imarkdownx.data.dao

import androidx.room.*
import xyz.aiinirii.imarkdownx.entity.User

/**
 *
 * @author AIINIRII
 */
@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("select * from User where id=:id")
    fun findUserById(id: Long): User?
}