package xyz.aiinirii.imarkdownx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
class FileItemAdapter(private var fileItemList: List<File>?) : RecyclerView.Adapter<FileItemAdapter.ViewHolder>() {
    val sharedPreferences = IMarkdownXApplication.context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: MaterialTextView = view.findViewById(R.id.file_name)
        val fileDate: MaterialTextView = view.findViewById(R.id.file_date)
        val sync: ImageView = view.findViewById(R.id.sync)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem = fileItemList?.get(position)
        if (fileItem?.remoteId == null
            || sharedPreferences.getString("userLocalName", "")
            == IMarkdownXApplication.context.getString(R.string.default_username)
        ) {
            holder.sync.visibility = View.INVISIBLE
        }
        holder.fileName.text = fileItem?.name ?: ""
        holder.fileDate.text = fileItem?.date ?: ""
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

    override fun getItemCount(): Int = fileItemList?.size ?: 0

    fun setFileItemList(value: List<File>) {
        fileItemList = value
        notifyDataSetChanged()
    }

}