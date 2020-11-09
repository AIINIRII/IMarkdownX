package xyz.aiinirii.imarkdownx.ui.edit.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.fragment_edit_main.*
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentEditMainBinding
import xyz.aiinirii.imarkdownx.ui.edit.render.EditRenderActivity
import xyz.aiinirii.imarkdownx.utils.MarkwonBuilder

private const val TAG = "EditMainFragment"

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
        }

        val stateEditText = edittext_edit_main

        btn_undo.setOnClickListener {
            if (isNew) {
                stateEditText.undo(false)
            } else {
                stateEditText.undo(true)
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

        val markwon = MarkwonBuilder.markwon
        val markwonEditor = MarkwonEditor.create(markwon)

        edittext_edit_main.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(markwonEditor))

        toolbar_edit.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onStop() {
        super.onStop()
        fragmentEditMainBinding.viewModel?.saveFile()
    }

}