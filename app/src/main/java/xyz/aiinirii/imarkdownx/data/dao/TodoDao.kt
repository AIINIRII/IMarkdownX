package xyz.aiinirii.imarkdownx.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.aiinirii.imarkdownx.entity.Todo

/**
 *
 * @author AIINIRII
 */
@Dao
interface TodoDao {

    @Query("select * from todo")
    fun getTodos(): LiveData<List<Todo>>

    @Insert
    suspend fun insert(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Query("select * from todo where content=:content")
    suspend fun getTodoByContent(content: String): Todo?
}