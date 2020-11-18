package xyz.aiinirii.imarkdownx.ui.edit

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter
import xyz.aiinirii.imarkdownx.databinding.FragmentEditBinding
import xyz.aiinirii.imarkdownx.ui.changeprivatepassword.ChangePrivatePasswordActivity
import xyz.aiinirii.imarkdownx.ui.edit.main.EditMainActivity
import xyz.aiinirii.imarkdownx.ui.edit.privacy.PrivacyActivity

private const val TAG = "EditFragment"

class EditFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences

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

        // get sharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)
        // load user local id
        val userLocalId = sharedPreferences.getLong("userLocalId", -1L)

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
                        R.id.lock_item -> {
                            viewModel.lockItem(position)
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

        // setup when verified password's logic
        viewModel.privatePasswordVerified.observe(viewLifecycleOwner) {
            if (it == 1) {
                val intent = Intent(activity, PrivacyActivity::class.java)
                Toast.makeText(context, "enter the private mode", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            } else if (it == 2) {
                Toast.makeText(context, "password wrong", Toast.LENGTH_SHORT).show()
            }
        }

        // setup is have private password logic
        viewModel.isHavePrivatePassword.observe(viewLifecycleOwner) { isHavePrivatePassword ->
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_private_valid, null)
            when (isHavePrivatePassword) {
                // if the user have the private password
                1 -> {
                    // alert a dialog and let user enter
                    AlertDialog.Builder(requireActivity())
                        .setTitle("enter the password to private mode")
                        .setView(view)
                        .setPositiveButton("confirm") { _, _ ->
                            val password = view.findViewById<TextInputEditText>(R.id.dialog_password).text ?: ""
                            lifecycleScope.launch(Dispatchers.IO) {
                                if (userLocalId != -1L) {
                                    val verifyPrivatePassword =
                                        viewModel.verifyPrivatePassword(userLocalId, password.toString())
                                    if (verifyPrivatePassword) {
                                        viewModel.privatePasswordVerified.postValue(1)
                                    } else {
                                        viewModel.privatePasswordVerified.postValue(2)
                                    }
                                } else {
                                    Log.e(TAG, "onActivityCreated: no user local id find")
                                }
                            }
                        }
                        .setNegativeButton("cancel") { _, _ -> }
                        .show()
                }
                // if the user do not have the private password
                2 -> {
                    val intent = Intent(activity, ChangePrivatePasswordActivity::class.java)
                    intent.putExtra("is_new", true)
                    startActivity(intent)
                }
                else -> {
                    Log.e(TAG, "onActivityCreated: no such value for is have private password")
                }
            }
        }

        // setup edit toolbar menu action
        toolbar_edit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.private_mode -> {
                    viewModel.checkPrivatePassword(userLocalId)
                }
            }
            true
        }
    }

}