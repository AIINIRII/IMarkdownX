package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @author AIINIRII
 */
@Entity
class Folder(var name: String, var color: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "Folder(name='$name', color=$color, id=$id)"
    }

}