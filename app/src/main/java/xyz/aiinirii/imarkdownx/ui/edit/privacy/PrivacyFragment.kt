package xyz.aiinirii.imarkdownx.ui.edit.privacy

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter
import xyz.aiinirii.imarkdownx.databinding.FragmentPrivacyBinding
import xyz.aiinirii.imarkdownx.ui.changeprivatepassword.ChangePrivatePasswordActivity
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

        viewModel.folder.observe(viewLifecycleOwner) {
            it.observe(viewLifecycleOwner) {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.findFilesByFolder()
                }
            }
        }

        viewModel.filesInFolderInitialized.observe(viewLifecycleOwner) { filesInFolderIsInitialized ->
            if (filesInFolderIsInitialized) {
                viewModel.filesInFolder.observe(viewLifecycleOwner) {
                    fileItemAdapter.setFileItemList(it)
                }
            }
        }
        // set action when click file item
        fileItemAdapter.onItemClickListener = object : FileItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, EditMainActivity::class.java)
                intent.putExtra("is_new", false)
                intent.putExtra("file_id", viewModel.filesInFolder.value?.get(position)?.id)
                startActivity(intent)
            }
        }

        // set action when long click file item
        fileItemAdapter.onItemLongClickListener = object : FileItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.file_item_long_click_private_menu, popupMenu.menu)

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
            intent.putExtra("folder_id", viewModel.folder.value!!.value!!.id)
            intent.putExtra("is_new", true)
            intent.putExtra("is_privacy", true)
            startActivity(intent)
        }

        // setup edit toolbar menu action
        toolbar_edit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.lock_password_setting -> {
                    val intent = Intent(activity, ChangePrivatePasswordActivity::class.java)
                    intent.putExtra("is_new", false)
                    startActivityForResult(intent, 1)
                }
            }
            true
        }

        // setup back btn action
        toolbar_edit.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val result = data?.getBooleanExtra("isSuccess", false) ?: false
                if (result) {
                    Toast
                        .makeText(requireContext(), getString(R.string.toast_success_change_psw), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast
                        .makeText(requireContext(), getString(R.string.toast_failed_change_psw), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}