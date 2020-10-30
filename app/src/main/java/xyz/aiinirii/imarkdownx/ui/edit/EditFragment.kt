package xyz.aiinirii.imarkdownx.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_edit.*
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.adapter.FileItemAdapter

class EditFragment : Fragment() {

    companion object {
        fun newInstance() = EditFragment()
    }

    private lateinit var viewModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
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
        viewModel = ViewModelProvider(this, EditViewModelFactory).get(EditViewModel::class.java)

        val fileItemAdapter = FileItemAdapter(viewModel.fileCatalog.value)
        viewModel.fileCatalog.observe(this.viewLifecycleOwner) {
            fileItemAdapter.setFileItemList(it)
        }
        // set recycler list view
        val linearLayoutManager = LinearLayoutManager(this.context)
        listview_files.layoutManager = linearLayoutManager
        listview_files.adapter = fileItemAdapter
    }

}