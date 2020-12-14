package xyz.aiinirii.imarkdownx.ui.user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var fragmentRegisterBinding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.fragment_register, container, false)
        fragmentRegisterBinding = FragmentRegisterBinding.bind(inflate).apply {
            viewModel = ViewModelProvider(this@RegisterFragment).get(RegisterViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentRegisterBinding.viewModel!!

        viewModel.startCancel.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.endRegister.observe(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

}