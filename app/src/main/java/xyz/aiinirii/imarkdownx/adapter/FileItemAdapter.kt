package xyz.aiinirii.imarkdownx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.entity.File

/**
 *
 * @author AIINIRII
 */
class FileItemAdapter(private var fileItemList: List<File>?) : RecyclerView.Adapter<FileItemAdapter.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: MaterialTextView = view.findViewById(R.id.file_name)
        val fileDate: MaterialTextView = view.findViewById(R.id.file_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem = fileItemList?.get(position)
        holder.fileName.text = fileItem?.name ?: ""
        holder.fileDate.text = fileItem?.date ?: ""
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener!!.onItemClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int = fileItemList?.size ?: 0

    fun setFileItemList(value: List<File>) {
        fileItemList = value
        notifyDataSetChanged()
    }


}