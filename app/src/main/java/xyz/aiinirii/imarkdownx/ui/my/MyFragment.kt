package xyz.aiinirii.imarkdownx.ui.my

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.aiinirii.imarkdownx.ui.user.changepassword.ChangePasswordActivity
import xyz.aiinirii.imarkdownx.IMarkdownXApplication
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.data.model.CommonResult
import xyz.aiinirii.imarkdownx.databinding.FragmentMyBinding
import xyz.aiinirii.imarkdownx.ui.user.login.LoginActivity
import xyz.aiinirii.imarkdownx.ui.user.register.RegisterActivity
import xyz.aiinirii.imarkdownx.ui.user.updateinfo.UpdateInfoActivity

class MyFragment : Fragment() {

    companion object {
        fun newInstance() = MyFragment()
    }

    private lateinit var fragmentMyBinding: FragmentMyBinding
    private val sharedPreferences =
        IMarkdownXApplication.context.getSharedPreferences("IMarkdownX", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my, container, false)
        fragmentMyBinding = FragmentMyBinding.bind(root).apply {
            viewModel = ViewModelProvider(this@MyFragment).get(MyViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = fragmentMyBinding.viewModel!!

        viewModel.setUsername(sharedPreferences.getString("userLocalName", "")!!)

        viewModel.loginStart.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, LoginActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }

        viewModel.registerStart.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.updateInfoStart.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, UpdateInfoActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.changePasswordStart.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.deleteStart.observe(viewLifecycleOwner) {
            if (it) {
                AlertDialog.Builder(requireActivity())
                    .setTitle(requireActivity().getString(R.string.delete_account_ensure_title))
                    .setMessage(requireActivity().getString(R.string.delete_account_ensure_message))
                    .setPositiveButton(requireActivity().getString(R.string.btn_confirm)) { _, _ ->
                        viewModel.deleteUser()
                    }
                    .setNegativeButton(requireActivity().getString(R.string.btn_cancel)) { _, _ ->
                        return@setNegativeButton
                    }
                    .show()
            }
        }

        viewModel.isSyncing.observe(viewLifecycleOwner) {
            if (it) {

            } else {
                Toast.makeText(
                    IMarkdownXApplication.context,
                    IMarkdownXApplication.context.getString(R.string.sync_success_toast),
                    Context.MODE_PRIVATE
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val viewModel = fragmentMyBinding.viewModel!!
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.setUsername(sharedPreferences.getString("userLocalName", "")!!)
                }
            }
        }
    }
}