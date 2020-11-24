package xyz.aiinirii.imarkdownx.ui.changeprivatepassword

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_change_private_password.*
import xyz.aiinirii.imarkdownx.R
import xyz.aiinirii.imarkdownx.databinding.FragmentChangePrivatePasswordBinding
import xyz.aiinirii.imarkdownx.utils.MD5Utils

private const val TAG = "ChangePrivatePasswordFr"

class ChangePrivatePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePrivatePasswordFragment()
    }

    private lateinit var changePrivatePasswordBinding: FragmentChangePrivatePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_change_private_password, container, false)
        changePrivatePasswordBinding = FragmentChangePrivatePasswordBinding.bind(root).apply {
            viewModel = ViewModelProvider(requireActivity()).get(ChangePrivatePasswordViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val extras = requireActivity().intent.extras

        val isNew = extras?.getBoolean("is_new") ?: false

        val viewModel = changePrivatePasswordBinding.viewModel!!

        if (isNew) {
            origin_password.visibility = View.GONE
            Log.i(TAG, "onActivityCreated: ${MD5Utils.getMD5Code("")}")
            viewModel.originPassword.postValue("")
        }

        viewModel.isCancel.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent()
                intent.putExtra("isSuccess", false)
                requireActivity().apply {
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }

        viewModel.isConfirm.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.originPassword.value == null || viewModel.newConfirmPassword.value == null || viewModel.newPassword.value == null) {
                    Toast.makeText(requireContext(), "text field should not be empty", Toast.LENGTH_SHORT).show()
                } else if (!viewModel.verifyConfirmPassword()) {
                    Toast.makeText(
                        requireContext(),
                        "confirm password should be same as new password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.changePassword()
                }
            }
        }

        viewModel.isChangingSuccess.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent()
                intent.putExtra("isSuccess", true)
                requireActivity().apply {
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {
                Toast.makeText(requireContext(), "wrong password", Toast.LENGTH_SHORT).show()
            }
        }
    }

}