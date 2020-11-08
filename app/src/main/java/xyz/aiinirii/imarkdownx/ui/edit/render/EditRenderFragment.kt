package xyz.aiinirii.imarkdownx.ui.edit.render

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.noties.markwon.Markwon
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentEditRenderBinding

class EditRenderFragment : Fragment() {

    lateinit var fragmentEditRenderBinding: FragmentEditRenderBinding
    lateinit var markwon: Markwon

    companion object {
        fun newInstance() = EditRenderFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_edit_render, container, false)
        fragmentEditRenderBinding = FragmentEditRenderBinding.bind(root).apply {
            viewModel = (activity as EditRenderActivity).obtainViewModel()
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // init markwon
        markwon = Markwon.create(requireContext())

        val intent = requireActivity().intent
        val extras = intent.extras!!

        val fileContent = extras.getString("file_content")
        val fileTitle = extras.getString("file_title")

        fragmentEditRenderBinding.viewModel?.renderText(markwon, fileContent.toString())
        fragmentEditRenderBinding.viewModel?.fileTitle?.postValue(fileTitle)

    }

}