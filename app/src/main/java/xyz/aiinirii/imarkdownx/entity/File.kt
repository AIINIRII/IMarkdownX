package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

/**
 * File Item
 * @author AIINIRII
 */
@Entity
class File(
    var name: String,
    var date: String,
    var content: String,
    var locked: Boolean = false,
    var remoteId: Long? = null
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ForeignKey(
        entity = Folder::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    var folderId: Long = 0


    override fun toString(): String {
        return "File(name='$name', date='$date', content='$content', locked=$locked, id=$id, folderId=$folderId)"
    }
}