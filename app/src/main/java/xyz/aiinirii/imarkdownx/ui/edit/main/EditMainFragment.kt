package xyz.aiinirii.imarkdownx.ui.edit.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentEditMainBinding
import xyz.aiinirii.imarkdownx.ui.edit.render.EditRenderActivity

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

        if (!isNew) {
            // if it is not new file, load it.
            val fileId = extras.getLong("file_id")
            Log.d(TAG, "onViewCreated: get file id $fileId")
            val file = fragmentEditMainBinding.viewModel?.getFileById(fileId)
            if (file != null) {
                file.observe(viewLifecycleOwner) {
                    Log.d(TAG, "onViewCreated: file content: ${it.content}")
                    fragmentEditMainBinding.viewModel?.fileContent?.postValue(it.content)
                    fragmentEditMainBinding.viewModel?.fileName?.postValue(it.name)
                }
            } else {
                Log.w(TAG, "onViewCreated: can not file the file with this id")
            }
        }

        // set an observer to renderFile
        fragmentEditMainBinding.viewModel?.renderFile?.observe(viewLifecycleOwner) {
            // if the renderFile arg change to true, got to EditRenderActivity
            if (it) {
                Log.d(TAG, "onActivityCreated: create an intent to EditRenderActivity")
                val intent = Intent(activity, EditRenderActivity::class.java)
                intent.putExtra("file_title", fragmentEditMainBinding.viewModel?.fileName?.value)
                intent.putExtra("file_content", fragmentEditMainBinding.viewModel?.fileContent?.value)
                startActivity(intent)
            }
        }
    }

}