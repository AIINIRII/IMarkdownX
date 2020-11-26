package xyz.aiinirii.imarkdownx.base

import android.annotation.TargetApi
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import xyz.aiinirii.imarkdownx.R


/**
 *
 * @author AIINIRII
 */
@TargetApi(23)
class BaseFingerprintDialog : DialogFragment() {

    private var cancellationSignal = CancellationSignal()
    var onAuthenticationSucceededListener: OnAuthenticationSucceededListener? = null
    var onAuthenticationHelpListener: OnAuthenticationHelpListener? = null
    var onAuthenticationErrorListener: OnAuthenticationErrorListener? = null
    var onAuthenticationFailedListener: OnAuthenticationFailedListener? = null

    interface OnAuthenticationSucceededListener {
        fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?)
    }

    interface OnAuthenticationHelpListener {
        fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
    }

    interface OnAuthenticationErrorListener {
        fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
    }

    interface OnAuthenticationFailedListener {
        fun onAuthenticationFailed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fingerprint, container, false)
        view.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        return view
    }

    private fun stopListening() {
        cancellationSignal.cancel()
    }

    override fun onResume() {
        super.onResume()
        cancellationSignal = CancellationSignal()
        startListening()
    }

    private fun startListening() {
        val fingerprintManager = requireContext().getSystemService(FingerprintManager::class.java)
        fingerprintManager.authenticate(null, cancellationSignal, 0, object :
            FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                this@BaseFingerprintDialog.onAuthenticationErrorListener
                    ?.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                this@BaseFingerprintDialog.onAuthenticationHelpListener
                    ?.onAuthenticationHelp(helpCode, helpString)
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                this@BaseFingerprintDialog.onAuthenticationSucceededListener
                    ?.onAuthenticationSucceeded(result)
            }

            override fun onAuthenticationFailed() {
                this@BaseFingerprintDialog.onAuthenticationFailedListener
                    ?.onAuthenticationFailed()
            }
        }, null)
    }
}