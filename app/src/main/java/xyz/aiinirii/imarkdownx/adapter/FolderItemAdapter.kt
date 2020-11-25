package xyz.aiinirii.imarkdownx.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.aiinirii.imarkdownx.IMarkdownXApplication.Companion.context
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.entity.Folder

// todo: implement folder item adapter and implement the folder list
/**
 *
 * @author AIINIRII
 */
class FolderItemAdapter (private var folderItemList: List<Folder>?) : RecyclerView.Adapter<FolderItemAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folderName: MaterialTextView = view.findViewById(R.id.folder_name)
        val folderColor: LinearLayout = view.findViewById(R.id.folder_color)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folderItem = folderItemList?.get(position)
        holder.folderName.text = folderItem?.name ?: ""
        holder.folderColor.background = folderItem?.color?.let { ColorDrawable(it) }
            ?: ContextCompat.getDrawable(context, R.color.colorPrimary)
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

    override fun getItemCount(): Int = folderItemList?.size ?: 0

    fun setFolderItemList(value: List<Folder>) {
        folderItemList = value
        notifyDataSetChanged()
    }

}