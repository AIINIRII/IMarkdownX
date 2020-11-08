package xyz.aiinirii.imarkdownx.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

/**
 * File Item
 * @author AIINIRII
 */
@Entity
class File(var name: String, var date: String, var content: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}