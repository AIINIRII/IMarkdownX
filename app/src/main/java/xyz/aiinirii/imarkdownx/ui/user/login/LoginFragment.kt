package xyz.aiinirii.imarkdownx.ui.user.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentLoginBinding
import xyz.aiinirii.imarkdownx.ui.user.register.RegisterActivity

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var fragmentLoginBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(R.layout.fragment_login, container, false)
        fragmentLoginBinding = FragmentLoginBinding.bind(inflate).apply {
            viewModel = ViewModelProvider(this@LoginFragment).get(LoginViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return fragmentLoginBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModel = fragmentLoginBinding.viewModel!!

        viewModel.endLogin.observe(viewLifecycleOwner) {
            if (it) {
                val intent = requireActivity().intent
                requireActivity().setResult(Activity.RESULT_OK)
                requireActivity().finish()
            }
        }

        viewModel.startRegister.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

}