package xyz.aiinirii.imarkdownx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.entity.Todo

/**
 *
 * @author AIINIRII
 */
class TodoItemAdapter(private var todoItemList: List<Todo>?) :
    RecyclerView.Adapter<TodoItemAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val select: MaterialCheckBox = view.findViewById(R.id.checkbox)
        val todoContent: TextInputEditText = view.findViewById(R.id.todo_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoItem = todoItemList?.get(position)

        todoItem?.done?.let { holder.select.isChecked = it }
        holder.todoContent.setText(todoItem?.content ?: "")

        holder.todoContent.doOnTextChanged { text, _, _, _ ->
            todoItem?.content = text.toString()
        }

        holder.select.setOnCheckedChangeListener { _, isChecked ->
            todoItem?.done = isChecked
            if (isChecked) {
                holder.todoContent.setTextColor(IMarkdownXApplication.context.getColor(R.color.colorAccent))
            } else {
                holder.todoContent.setTextColor(IMarkdownXApplication.context.getColor(R.color.colorDark))
            }
        }

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClick(it, position)
            }
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                onItemLongClickListener!!.onItemLongClick(it, position)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int = todoItemList?.size ?: 0

    fun setTodoItemList(value: List<Todo>) {
        todoItemList = value
        notifyDataSetChanged()
    }

    fun getTodoItemList() = todoItemList

}