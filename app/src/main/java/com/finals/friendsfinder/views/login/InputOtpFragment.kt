package com.finals.friendsfinder.views.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.databinding.FragmentInputOtpBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class InputOtpFragment : BaseFragment<FragmentInputOtpBinding>() {
    companion object {
        private const val VERIFICATION_CODE = "VERIFICATION_CODE"
        fun newInstance(): InputOtpFragment {
            val arg = Bundle().apply {

            }
            return InputOtpFragment().apply {
                arguments = arg
            }
        }
    }

    var callBackOtp: ((otp: String) -> Unit)? = null

    override fun observeHandle() {
        super.observeHandle()

    }

    override fun setupView() {
        super.setupView()
        with(rootView) {
            edtOtp.setLabel("Enter OTP")
            edtOtp.setHint("Enter otp here")
            edtOtp.setInputType(InputType.TYPE_CLASS_PHONE)
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            layoutHeader.tvMessage.text = "OTP"
        }
    }

    override fun bindData() {
        super.bindData()
        with(rootView) {
            btnConfirm.clickWithDebounce {
                val otp = edtOtp.getText()
                callBackOtp?.invoke(otp)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentInputOtpBinding {
        return FragmentInputOtpBinding.inflate(inflater)
    }
}