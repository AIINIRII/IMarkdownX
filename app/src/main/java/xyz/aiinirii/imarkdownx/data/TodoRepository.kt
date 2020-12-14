package xyz.aiinirii.imarkdownx.data

import androidx.lifecycle.LiveData
import xyz.aiinirii.imarkdownx.data.dao.TodoDao
import xyz.aiinirii.imarkdownx.entity.Todo

/**
 *
 * @author AIINIRII
 */
class TodoRepository(private val todoDao: TodoDao) {

    fun getTodos(): LiveData<List<Todo>> {
        return todoDao.getTodos()
    }

    suspend fun insert(todo: Todo) {
        return todoDao.insert(todo)
    }

    suspend fun delete(todo: Todo) {
        return todoDao.delete(todo)
    }

    suspend fun update(todo: Todo) {
        return todoDao.update(todo)
    }

    suspend fun insertWithSearch(todo: Todo) {
        if (todoDao.getTodoByContent(todo.content) == null) {
            todoDao.insert(todo)
        }
    }
}