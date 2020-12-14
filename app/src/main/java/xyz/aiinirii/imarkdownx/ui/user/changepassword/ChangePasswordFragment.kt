package xyz.aiinirii.imarkdownx.ui.user.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    private lateinit var fragmentChangePasswordBinding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.fragment_change_password, container, false)
        fragmentChangePasswordBinding = FragmentChangePasswordBinding.bind(inflate).apply {
            viewModel = ViewModelProvider(this@ChangePasswordFragment).get(ChangePasswordViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentChangePasswordBinding.viewModel!!
        viewModel.endChange.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.startCancel.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

}