package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @author AIINIRII
 */
@Entity
class Todo(var content: String, var done: Boolean = false) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "Todo(content='$content', id=$id)"
    }
}