package xyz.aiinirii.imarkdownx.ui.user.updateinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentUpdateInfoBinding

class UpdateInfoFragment : Fragment() {

    companion object {
        fun newInstance() = UpdateInfoFragment()
    }

    private lateinit var fragmentUpdateInfoBinding: FragmentUpdateInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.fragment_update_info, container, false)
        fragmentUpdateInfoBinding = FragmentUpdateInfoBinding.bind(inflate).apply {
            viewModel = ViewModelProvider(this@UpdateInfoFragment).get(UpdateInfoViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentUpdateInfoBinding.viewModel!!

        viewModel.startCancel.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.endUpdate.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

}