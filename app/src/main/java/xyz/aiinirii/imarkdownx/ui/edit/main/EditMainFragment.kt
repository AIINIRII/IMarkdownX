package xyz.aiinirii.imarkdownx.ui.edit.main

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.fragment_edit_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.DialogFileInfoBinding
import xyz.aiinirii.imarkdownx.databinding.DialogTableInfoBinding
import xyz.aiinirii.imarkdownx.databinding.FragmentEditMainBinding
import xyz.aiinirii.imarkdownx.ui.edit.render.EditRenderActivity
import xyz.aiinirii.imarkdownx.utils.MarkwonBuilder
import java.io.*


private const val TAG = "EditMainFragment"
private const val SELECT_PIC = 1001

class EditMainFragment : Fragment() {
    companion object {
        fun newInstance() = EditMainFragment()
    }

    private lateinit var fragmentEditMainBinding: FragmentEditMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.fragment_edit_main, container, false)
        fragmentEditMainBinding = FragmentEditMainBinding.bind(inflate).apply {
            viewModel = (activity as EditMainActivity).obtainViewModel()
            lifecycleOwner = viewLifecycleOwner
        }
        return fragmentEditMainBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val extras: Bundle = activity?.intent?.extras!!
        val isNew = extras.getBoolean("is_new")
        val isPrivacy = extras.getBoolean("is_privacy")

        val viewModel = fragmentEditMainBinding.viewModel!!
        if (!isNew) {
            // if it is not new file, load it.
            val fileId = extras.getLong("file_id")
            Log.d(TAG, "onViewCreated: get file id $fileId")
            val file = viewModel.getFileById(fileId)
            if (file != null) {
                file.observe(viewLifecycleOwner) {
                    Log.d(TAG, "onViewCreated: file content: ${it.content}")
                    viewModel.fileContent.postValue(it.content)
                    viewModel.fileName.postValue(it.name)
                }
            } else {
                Log.w(TAG, "onViewCreated: can not file the file with this id")
            }
        } else {
            if (isPrivacy) {
                viewModel.locked.postValue(true)
            } else {
                viewModel.locked.postValue(false)
            }
        }

        val stateEditText = edittext_edit_main

        btn_undo.setOnClickListener {
            if (isNew) {
                stateEditText.undo(false)
            } else {
                stateEditText.undo(true)
            }
        }

        btn_redo.setOnClickListener {
            if (isNew) {
                stateEditText.redo(false)
            } else {
                stateEditText.redo(true)
            }
        }

        // if saved, create a toast
        viewModel.isSaved.observe(viewLifecycleOwner, {
            if (it) {
                val toast = Toast.makeText(context, getString(R.string.toast_saved_success), Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
                viewModel.isSaved.postValue(false)
            }
        })

        // set an observer to renderFile
        viewModel.renderFile.observe(viewLifecycleOwner) {
            // if the renderFile arg change to true, got to EditRenderActivity
            if (it) {
                Log.d(TAG, "onActivityCreated: create an intent to EditRenderActivity")
                val intent = Intent(activity, EditRenderActivity::class.java)
                intent.putExtra("file_title", viewModel.fileName.value)
                intent.putExtra("file_content", viewModel.fileContent.value)
                startActivity(intent)
            }
        }

        viewModel.deleteState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.delete()
                viewModel.deleteState.postValue(false)
            }
        }
        viewModel.quoteState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.quote()
                viewModel.quoteState.postValue(false)
            }
        }
        viewModel.headingState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.heading()
                viewModel.headingState.postValue(false)
            }
        }
        viewModel.boldState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.bold()
                viewModel.boldState.postValue(false)
            }
        }
        viewModel.italicState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.italic()
                viewModel.italicState.postValue(false)
            }
        }

        var alertDialogFile: AlertDialog? = null

        viewModel.fileState.observe(viewLifecycleOwner) {
            if (it) {
                val alertDialogFileBuilder = initFileAlertDialog(viewModel)
                alertDialogFile = alertDialogFileBuilder
                    ?.show()
                viewModel.fileState.postValue(false)
            }
        }

        viewModel.loadFromInternetState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.image()
                alertDialogFile?.dismiss()
                alertDialogFile = null
                viewModel.loadFromPhotoState.postValue(false)
            }
        }

        viewModel.loadFromPhotoState.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, SELECT_PIC)
                alertDialogFile?.dismiss()
                alertDialogFile = null
                viewModel.loadFromPhotoState.postValue(false)
            }
        }

        viewModel.codeState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.code()
                viewModel.codeState.postValue(false)
            }
        }
        viewModel.linkState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.link()
                viewModel.linkState.postValue(false)
            }
        }
        viewModel.linkAltState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.linkAlt()
                viewModel.linkAltState.postValue(false)
            }
        }
        viewModel.listState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.list()
                viewModel.listState.postValue(false)
            }
        }

        viewModel.hrState.observe(viewLifecycleOwner) {
            if (it) {
                stateEditText.hr()
                viewModel.hrState.postValue(false)
            }
        }

        var alertDialogTable: AlertDialog? = null

        viewModel.tableState.observe(viewLifecycleOwner) {
            if (it) {
                val alertDialogBuilder = initTableAlertDialog(viewModel)
                if (alertDialogBuilder != null) {
                    alertDialogTable = alertDialogBuilder.show()
                }
                viewModel.tableState.postValue(false)
            }
        }

        viewModel.confirmTableState.observe(viewLifecycleOwner) {
            if (it) {
                alertDialogTable?.dismiss()
                alertDialogTable = null
                stateEditText.table(viewModel.tableRow.value?.toInt() ?: 0, viewModel.tableCol.value?.toInt() ?: 0)
            }
        }

        viewModel.cancelTableCreateState.observe(viewLifecycleOwner) {
            if (it) {
                alertDialogTable?.dismiss()
                alertDialogTable = null
                viewModel.cancelTableCreateState.postValue(false)
            }
        }

        viewModel.imageUri.observe(viewLifecycleOwner) {
            stateEditText.image(it)
        }

        val markwon = MarkwonBuilder.markwon
        val markwonEditor = MarkwonEditor.create(markwon)

        edittext_edit_main.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(markwonEditor))

        toolbar_edit.setNavigationOnClickListener {
            requireActivity().finish()
        }

        toolbar_edit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_render -> {
                    viewModel.renderFile()
                }
                else -> {
                }
            }
            true
        }
    }

    private fun initFileAlertDialog(viewModel: EditMainViewModel): AlertDialog.Builder? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_file_info, null)
        val dialogFileInfoBinding = DialogFileInfoBinding.bind(view).apply {
            this.viewModel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }
        return AlertDialog.Builder(context)
            .setView(dialogFileInfoBinding.root)
    }

    private fun initTableAlertDialog(viewModel: EditMainViewModel): AlertDialog.Builder? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_table_info, null)
        val dialogTableInfoBinding = DialogTableInfoBinding.bind(view).apply {
            this.viewModel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }
        return AlertDialog.Builder(context)
            .setView(dialogTableInfoBinding.root)
    }

    override fun onStop() {
        fragmentEditMainBinding.viewModel?.saveFile()
        super.onStop()
    }

    override fun onDestroyView() {
        fragmentEditMainBinding.viewModel?.saveFile()
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            SELECT_PIC -> {
                Log.d(TAG, "onActivityResult: get the result of select pic")
                val dataUri = data?.data

                val fileUri = loadFileUri(dataUri)
                lifecycleScope.launch(Dispatchers.IO) {
                    Thread.sleep(200)
                    fragmentEditMainBinding.viewModel!!.setImageUri(fileUri.toString())
                }
            }
            else -> {
                Log.e(TAG, "onActivityResult: wrong with activity result")
            }
        }
    }

    private fun loadFileUri(dataUri: Uri?): Uri? {
        val inputStream: InputStream
        return try {
            inputStream = dataUri?.let { IMarkdownXApplication.context.contentResolver.openInputStream(it) }!!
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            val tmDir = File(IMarkdownXApplication.context.filesDir.path + "/image/")
            if (!tmDir.exists()) {
                tmDir.mkdir()
            }
            val img = File(tmDir.absolutePath + "/" + System.currentTimeMillis() + ".png")
            val fileOutputStream = FileOutputStream(img)
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Uri.fromFile(img)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}