package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * File Item
 * @author AIINIRII
 */
@Entity
class File(var name: String, var date: String, var content: String, var locked: Boolean = false) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "File(name='$name', date='$date', content='$content', locked=$locked, id=$id)"
    }
}