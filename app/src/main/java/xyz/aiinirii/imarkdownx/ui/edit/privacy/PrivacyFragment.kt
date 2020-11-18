package xyz.aiinirii.imarkdownx.ui.edit.privacy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_edit.*
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter
import xyz.aiinirii.imarkdownx.databinding.FragmentPrivacyBinding
import xyz.aiinirii.imarkdownx.ui.edit.main.EditMainActivity

class PrivacyFragment : Fragment() {

    companion object {
        fun newInstance() = PrivacyFragment()
    }

    lateinit var fragmentPrivacyBinding: FragmentPrivacyBinding

    private lateinit var viewModel: PrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_privacy, container, false)
        fragmentPrivacyBinding = FragmentPrivacyBinding.bind(root).apply {
            this.viewModel = ViewModelProvider(requireActivity()).get(PrivacyViewModel::class.java)
            this.lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentPrivacyBinding.viewModel!!

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
        fileItemAdapter.onItemLongClickListener = object : FileItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.item_long_click_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_item -> {
                            viewModel.deleteItem(position)
                        }
                        R.id.unlock_item -> {
                            viewModel.unlockItem(position)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        // set refresh action
        swipe_refresh_edit.setOnRefreshListener {
            viewModel.refresh()
            swipe_refresh_edit.isRefreshing = false
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

        // setup edit toolbar menu action
        toolbar_edit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.lock_password_setting -> {
                    /*
                     todo 设置用户密码
                     1. 确认用户旧密码
                     2. 修改密码
                     */
                }
            }
            true
        }

        // setup back btn action
        toolbar_edit.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

}