package xyz.aiinirii.imarkdownx.ui.todo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.TodoItemAdapter
import xyz.aiinirii.imarkdownx.data.DatabaseRepository
import xyz.aiinirii.imarkdownx.data.FileRepository
import xyz.aiinirii.imarkdownx.data.TodoRepository
import xyz.aiinirii.imarkdownx.databinding.FragmentTodoBinding
import xyz.aiinirii.imarkdownx.entity.Todo
import java.util.*

class TodoFragment : Fragment() {

    companion object {
        fun newInstance() = TodoFragment()
    }

    private lateinit var fragmentTodoBinding: FragmentTodoBinding
    private val todoRepository: TodoRepository = TodoRepository(DatabaseRepository.todoDao())
    private val fileRepository: FileRepository = FileRepository(DatabaseRepository.fileDao())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_todo, container, false)
        fragmentTodoBinding = FragmentTodoBinding.bind(inflate).apply {
            viewModel = ViewModelProvider(this@TodoFragment).get(TodoViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = fragmentTodoBinding.viewModel!!

        val todoList = MutableLiveData<LiveData<List<Todo>>>()

        val todoItemAdapter = TodoItemAdapter(null)

        todoItemAdapter.onItemLongClickListener = object : TodoItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.todo_item_long_click_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_item -> {
                            val todoItem = todoList.value!!.value!![position]
                            GlobalScope.launch(Dispatchers.IO) {
                                todoRepository.delete(todoItem)
                                todoList.postValue(todoRepository.getTodos())
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        val linearLayoutManagerFolderItemAdapter = LinearLayoutManager(this.context)
        listview_todos.layoutManager = linearLayoutManagerFolderItemAdapter
        listview_todos.adapter = todoItemAdapter

        todoList.observe(viewLifecycleOwner) { liveData ->
            liveData.observe(viewLifecycleOwner) {
                todoItemAdapter.setTodoItemList(it)
            }
        }

        todoList.postValue(todoRepository.getTodos())

        fab_add_todo.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                todoRepository.insert(
                    Todo("", false)
                )
                todoList.postValue(todoRepository.getTodos())
            }
        }

        toolbar_todo.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_save -> {
                    todoList.value!!.value!!.forEach { todo ->
                        GlobalScope.launch(Dispatchers.IO) {
                            todoRepository.update(todo)
                        }
                    }
                    Toast.makeText(
                        context,
                        getString(R.string.toast_saved_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.btn_import -> {
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton(getString(R.string.btn_confirm)) { _, _ ->
                            importTodoListFromFiles(todoList)
                        }
                        .setNegativeButton(getString(R.string.btn_cancel)) { _, _ -> }
                        .setMessage(getString(R.string.import_todo_alert))
                        .show()
                    true
                }
                R.id.btn_delete -> {
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton(getString(R.string.btn_confirm)) { _, _ ->
                            GlobalScope.launch(Dispatchers.IO) {
                                todoList.value!!.value!!.forEach { todo ->
                                    if (todo.done) {
                                        todoRepository.delete(todo)
                                    }
                                }
                            }
                        }
                        .setNegativeButton(getString(R.string.btn_cancel)) { _, _ -> }
                        .setMessage(getString(R.string.delete_confirm_toast))
                        .show()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun importTodoListFromFiles(todoList: MutableLiveData<LiveData<List<Todo>>>) {
        GlobalScope.launch(Dispatchers.IO) {
            val allUnlockedFiles = fileRepository.getAllUnlockedFiles()
            val regex = Regex("((- \\[[x ]]).*(\n))|((- \\[[x ]]).*$)")
            allUnlockedFiles.forEach {
                regex.findAll(it.content).forEach { matchResult ->
                    val trim = matchResult.value.trim()
                    val todo = Todo("", false)
                    todo.done = trim[3] == 'x' || trim[3] == 'X'
                    todo.content = trim.subSequence(6, trim.length).toString()
                    todoRepository.insertWithSearch(todo)
                }
            }
            todoList.postValue(todoRepository.getTodos())
        }
    }

}