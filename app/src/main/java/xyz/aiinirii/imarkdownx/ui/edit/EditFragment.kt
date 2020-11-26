package xyz.aiinirii.imarkdownx.ui.edit

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.dialog_private_valid.view.*
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter
import xyz.aiinirii.imarkdownx.adapter.FolderItemAdapter
import xyz.aiinirii.imarkdownx.base.BaseFingerprintDialog
import xyz.aiinirii.imarkdownx.databinding.FragmentEditBinding
import xyz.aiinirii.imarkdownx.ui.changeprivatepassword.ChangePrivatePasswordActivity
import xyz.aiinirii.imarkdownx.ui.edit.main.EditMainActivity
import xyz.aiinirii.imarkdownx.ui.edit.privacy.PrivacyActivity

private const val TAG = "EditFragment"

class EditFragment : Fragment() {


    lateinit var sharedPreferences: SharedPreferences

    lateinit var fragmentEditBinding: FragmentEditBinding

    var firstStart = true

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


        // add folder
        fab_add_folder.setOnClickListener {
            val folderAddAlertDialog: AlertDialog
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_folder_info, null)
            folderAddAlertDialog = AlertDialog.Builder(context)
                .setView(view)
                .show()
            view.findViewById<MaterialButton>(R.id.btn_confirm).setOnClickListener {
                viewModel.createFolder(view.findViewById<TextInputEditText>(R.id.dialog_folder_name).text.toString())
                folderAddAlertDialog.dismiss()
            }
            view.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
                folderAddAlertDialog.dismiss()
            }
        }

        // init folder item adapter
        viewModel.folders.observe(viewLifecycleOwner) {
            val folderItemAdapter = FolderItemAdapter(null)
            initFolderItemClickAction(folderItemAdapter, viewModel)
            listview_folders.adapter = folderItemAdapter
            it.observe(viewLifecycleOwner) { folderList ->
                folderItemAdapter.setFolderItemList(folderList)
            }
        }

        // set recycler list view
        val linearLayoutManagerFolderItemAdapter = LinearLayoutManager(this.context)
        listview_folders.layoutManager = linearLayoutManagerFolderItemAdapter

        // init file item adapter
        val fileItemAdapter = FileItemAdapter(null)


        viewModel.folder.observe(viewLifecycleOwner) { folderLiveData ->
            folderLiveData.observe(viewLifecycleOwner) {
                Log.d(TAG, "onActivityCreated: switch to folder: $it")
                viewModel.toolbarTitle.postValue(it?.name)
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.isLoadingFiles.postValue(true)
                    viewModel.findFilesByFolder()
                    viewModel.isLoadingFiles.postValue(false)
                }
            }
        }

        viewModel.isLoadingFiles.observe(viewLifecycleOwner) {
            swipe_refresh_edit.isRefreshing = it
        }

        viewModel.filesInFolderInitialized.observe(viewLifecycleOwner) { filesInFolderIsInitialized ->
            if (filesInFolderIsInitialized) {
                viewModel.filesInFolder.observe(viewLifecycleOwner) {
                    fileItemAdapter.setFileItemList(it)
                }
            }
        }

        initFileItemClickAction(fileItemAdapter, viewModel)

        // set refresh action
        swipe_refresh_edit.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.findFilesByFolder()
                swipe_refresh_edit.isRefreshing = false
            }
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
            intent.putExtra("is_privacy", false)
            startActivity(intent)
        }

        // setup when verified password's logic
        viewModel.privatePasswordVerified.observe(viewLifecycleOwner) {
            if (it == 1) {
                val intent = Intent(activity, PrivacyActivity::class.java)
                Toast.makeText(context, getString(R.string.toast_privacy_mode), Toast.LENGTH_SHORT).show()
                startActivity(intent)
                viewModel.initIsHavePrivatePassword()
            } else if (it == 2) {
                Toast.makeText(context, getString(R.string.toast_password_wrong), Toast.LENGTH_SHORT).show()
                viewModel.initIsHavePrivatePassword()
            }
        }

        viewModel.folderDeleteSignal.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, getString(R.string.toast_delete_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.toast_delete_failed), Toast.LENGTH_SHORT).show()
            }
        }

        // setup is have private password logic
        viewModel.isHavePrivatePassword.observe(viewLifecycleOwner) { isHavePrivatePassword ->
            var alertDialog: AlertDialog? = null
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_private_valid, null)
            view.btn_confirm.setOnClickListener {
                val password = view.findViewById<TextInputEditText>(R.id.dialog_password).text ?: ""
                GlobalScope.launch(Dispatchers.IO) {
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
                alertDialog?.dismiss()
            }
            view.btn_cancel.setOnClickListener {
                alertDialog?.dismiss()
            }
            when (isHavePrivatePassword) {
                // if the user have the private password
                1 -> {
                    if (supportFingerPrint()) {
                        val fingerprintDialog = BaseFingerprintDialog()
                        fingerprintDialog.onAuthenticationSucceededListener =
                            object : BaseFingerprintDialog.OnAuthenticationSucceededListener {
                                override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                                    viewModel.privatePasswordVerified.postValue(1)
                                    fingerprintDialog.dismiss()
                                }
                            }
                        fingerprintDialog.onAuthenticationFailedListener =
                            object : BaseFingerprintDialog.OnAuthenticationFailedListener {
                                override fun onAuthenticationFailed() {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.toast_failed_verify_fingerprint),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        fingerprintDialog.show(
                            requireActivity().supportFragmentManager,
                            getString(R.string.dialog_verify_fingerprint)
                        )
                    } else {
                        // alert a dialog and let user enter
                        alertDialog = AlertDialog.Builder(requireActivity())
                            .setView(view)
                            .show()
                    }
                }
                // if the user do not have the private password
                2 -> {
                    val intent = Intent(activity, ChangePrivatePasswordActivity::class.java)
                    intent.putExtra("is_new", true)
                    startActivity(intent)
                }
                else -> {
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

        val drawerToggle =
            object :
                ActionBarDrawerToggle(activity, drawer_layout, toolbar_edit, R.string.app_name, R.string.app_name) {

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    fab_add_folder.show()
                    fab_add_task.hide()
                }

                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    fab_add_task.show()
                    fab_add_folder.hide()
                }
            }

        drawerToggle.syncState()

        drawer_layout.addDrawerListener(drawerToggle)

        toolbar_edit.setNavigationOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun supportFingerPrint(): Boolean {
        val keyguardManager: KeyguardManager = requireContext().getSystemService(KeyguardManager::class.java)
        val fingerprintManager: FingerprintManager = requireContext().getSystemService(FingerprintManager::class.java)
        if (!fingerprintManager.isHardwareDetected) {
            // Toast.makeText(this, "your phone do not support finger", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "supportFingerPrint: your phone do not support fingerprint")
            return false
        } else if (!keyguardManager.isKeyguardSecure) {
            // Toast.makeText(this, "您还未设置锁屏，请先设置锁屏并添加一个指纹", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "supportFingerPrint: haven't set fingerprint")
            return false
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            // Toast.makeText(this, "您至少需要在系统设置中添加一个指纹", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "supportFingerPrint: have to add a fingerprint")
            return false
        }
        return true
    }

    private fun initFileItemClickAction(
        fileItemAdapter: FileItemAdapter,
        viewModel: EditViewModel
    ) {
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
                popupMenu.menuInflater.inflate(R.menu.file_item_long_click_menu, popupMenu.menu)

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
    }

    override fun onResume() {
        super.onResume()
        if (!firstStart) {
            lifecycleScope.launch(Dispatchers.IO) {
                fragmentEditBinding.viewModel?.findFilesByFolder()
            }
        } else {
            firstStart = false
        }
    }

    private fun initFolderItemClickAction(
        folderItemAdapter: FolderItemAdapter,
        viewModel: EditViewModel
    ) {
        // set click action
        folderItemAdapter.onItemClickListener = object : FolderItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val folder = viewModel.folders.value!!.value?.get(position)
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.updateFilesInFolderByFolder(folder)
                }
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }

        // set long click action
        folderItemAdapter.onItemLongClickListener = object : FolderItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.folder_item_long_click_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_item -> {
                            viewModel.deleteFolderItem(position)
                        }
                        R.id.change_color -> {
                            val folderColorInfoView =
                                LayoutInflater.from(context).inflate(R.layout.dialog_folder_color_info, null)
                            val folderColorAlertDialog = AlertDialog.Builder(context)
                                .setView(folderColorInfoView)
                                .show()
                            folderColorInfoView.apply {
                                findViewById<MaterialButton>(R.id.btn_confirm).setOnClickListener {
                                    val red =
                                        findViewById<TextInputEditText>(R.id.dialog_color_red).text.toString().toInt()
                                    val green =
                                        findViewById<TextInputEditText>(R.id.dialog_color_green).text.toString().toInt()
                                    val blue =
                                        findViewById<TextInputEditText>(R.id.dialog_color_blue).text.toString().toInt()
                                    viewModel.changeFolderColor(position, red, green, blue)
                                    folderColorAlertDialog.dismiss()
                                }
                                findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
                                    folderColorAlertDialog.dismiss()
                                }
                            }
                        }
                        R.id.change_name -> {
                            val folderInfoView =
                                LayoutInflater.from(context).inflate(R.layout.dialog_folder_info, null)
                            val folderAlertDialog = AlertDialog.Builder(context)
                                .setView(folderInfoView)
                                .show()
                            folderInfoView.apply {
                                findViewById<MaterialTextView>(R.id.title).text =
                                    context.getString(R.string.title_folder_change_name)
                                findViewById<MaterialButton>(R.id.btn_confirm).setOnClickListener {
                                    val folderName =
                                        findViewById<TextInputEditText>(R.id.dialog_folder_name).text.toString()
                                    viewModel.changeFolderName(position, folderName)
                                    folderAlertDialog.dismiss()
                                }
                                findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener {
                                    folderAlertDialog.dismiss()
                                }
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

}