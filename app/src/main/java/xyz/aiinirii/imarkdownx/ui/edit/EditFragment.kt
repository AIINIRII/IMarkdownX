package xyz.aiinirii.imarkdownx.ui.edit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_edit.*
import xyz.aiinirii.imarkdownx.MainActivity
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter
import xyz.aiinirii.imarkdownx.databinding.FragmentEditBinding
import xyz.aiinirii.imarkdownx.ui.edit.main.EditMainActivity


class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()
    }

    lateinit var fragmentEditBinding: FragmentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        fragmentEditBinding = FragmentEditBinding.bind(root).apply {
            this.viewModel = ViewModelProvider(requireActivity()).get(EditViewModel::class.java)
            this.lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        // bind toolbar and nav controller
        toolbar_edit
            .setupWithNavController(navController, appBarConfiguration)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentEditBinding.viewModel!!

        // init file item adapter
        val fileItemAdapter = FileItemAdapter(viewModel.files.value)
        viewModel.files.observe(this.viewLifecycleOwner) {
            fileItemAdapter.setFileItemList(it)
        }

        // set action when click file item
        fileItemAdapter.onItemClickListener = object : FileItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, EditMainActivity::class.java)
                intent.putExtra("is_new", false)
                intent.putExtra("file_id", viewModel.files.value?.get(position)?.id)
                startActivity(intent)
            }
        }

        // set action when long click file item
        fileItemAdapter.onItemLongClickListener = object : FileItemAdapter.OnItemLongClickListener{
            override fun onItemLongClick(view: View, position: Int) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.item_long_click_menu, popupMenu.menu)

                //弹出式菜单的菜单项点击事件

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId){
                        R.id.delete_item -> {
                            viewModel.deleteItem(position)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        // set recycler list view
        val linearLayoutManager = LinearLayoutManager(this.context)
        listview_files.layoutManager = linearLayoutManager
        listview_files.adapter = fileItemAdapter

        // set action when click fab_add_task
        fab_add_task.setOnClickListener {
            val intent = Intent(activity, EditMainActivity::class.java)
            intent.putExtra("is_new", true)
            startActivity(intent)
        }
    }

}