package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Item
 * @author AIINIRII
 */
@Entity
class User(var username: String, var privatePassword: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "User(username='$username', privatePassword='$privatePassword', id=$id)"
    }
}