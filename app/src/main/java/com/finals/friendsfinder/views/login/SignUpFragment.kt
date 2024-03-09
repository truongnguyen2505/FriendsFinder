package com.finals.friendsfinder.views.login

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import com.blankj.utilcode.util.ToastUtils
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.customizes.LoadingDialog
import com.finals.friendsfinder.databinding.FragmentSignUpBinding
import com.finals.friendsfinder.dialogs.NotifyDialog
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.commons.UserKey
import com.finals.friendsfinder.utilities.isValidEmail
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    companion object {
        private const val TAG = "SignUpFragment"
        private const val TIME_OUT = 60L
        fun newInstance(): SignUpFragment {
            val arg = Bundle()
            return SignUpFragment().apply {
                arguments = arg
            }
        }
    }

    private lateinit var dbReference: DatabaseReference
    private var verificationCode = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var auth: FirebaseAuth
    override fun observeHandle() {
        super.observeHandle()
        auth = Firebase.auth
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater)
    }

    override fun setupView() {
        super.setupView()
        setText()
        setListener()
    }

    private fun setListener() {
        with(rootView) {
            edtPass.setTypePassWord()
            edtConfirmPass.setTypePassWord()
            edtPhone.setInputType(InputType.TYPE_CLASS_PHONE)
            edtPhone.setFilterEdt(10)
            edtPass.showImagePassword(true)
            edtConfirmPass.showImagePassword(true)
            edtPass.setOnFocusChangeListener { view, b ->
                if (!b)
                    edtPass.hideMessageError()
            }
            edtPass.onTextChangeCallback = { txt ->
                if (txt.length < 8)
                    edtPass.setMessageError("Password must be greater than or equal to 8 characters!")
                else edtPass.hideMessageError()
            }
            edtPhone.onTextChangeCallback = { txt ->
                if (txt.length < 10) {
                    edtPhone.setMessageError("Phone number must be equal to 10 characters!")
                } else edtPhone.hideMessageError()
            }
            edtPhone.setOnFocusChangeListener { view, b ->
                if (!b)
                    edtPhone.hideMessageError()
            }
            btnSignUp.clickWithDebounce {
                checkEmptyToRegister()
            }
            btnBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun checkEmptyToRegister() {
        with(rootView) {
            val userName = edtName.getText()
            val email = edtEmail.getText()
            val pass = edtPass.getText()
            val cfPass = edtConfirmPass.getText()
            var phone = edtPhone.getText()
            if (userName.isEmpty()) {
                edtName.setMessageError("Username is not empty!")
                return
            }
            if (phone.isEmpty()) {
                edtPhone.setMessageError("Phone number is not empty!")
                return
            }
            if (email.isEmpty()) {
                edtEmail.setMessageError("Email is not empty!")
                return
            }
            if (!email.isValidEmail()) {
                edtEmail.setMessageError("Email is invalid!")
                return
            }
            if (pass.isEmpty()) {
                edtPass.setMessageError("Password is not empty!")
                return
            }
            if (pass.length < 8) {
                edtPass.setMessageError("Password has to contain from 8 characters!")
                return
            }
            if (cfPass.isEmpty()) {
                edtConfirmPass.setMessageError("Confirm is not empty!")
                return
            }
            if (!pass.equals(cfPass, true)) {
                edtConfirmPass.setMessageError("Password is not match!")
                return
            }
            val newPhone = if (phone.startsWith("0")) {
                "+84" + phone.removeRange(0, 1)
            } else return@with
            sendOtp(userName, email, pass, newPhone, phone)
        }

    }

    private fun sendOtp(
        userName: String,
        email: String,
        pass: String,
        newPhone: String,
        phoneNumber: String
    ) {
        LoadingDialog.show(requireContext())
        val builder = PhoneAuthOptions.Builder(auth)
            .setPhoneNumber(newPhone)
            .setTimeout(TIME_OUT, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    LoadingDialog.dismiss()
                    Log.d(TAG, "onVerificationCompleted: ")
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    LoadingDialog.dismiss()
                    Log.d(TAG, "onVerificationFailed: ")
                    showMessage("OTP verification failed!")
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d(TAG, "onCodeSent: ")
                    LoadingDialog.dismiss()
                    verificationCode = p0
                    resendToken = p1
                    ToastUtils.showShort("OTP sent successfully!")
                    val fragment = InputOtpFragment.newInstance()
                    fragment.callBackOtp = { otp ->
                        val credential = PhoneAuthProvider.getCredential(verificationCode, otp)
                        auth.signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "isSuccessful: ")
                                registerNewUser(userName, email, pass, phoneNumber)
                            } else {
                                Log.d(TAG, "OTP verification fail!")
                            }
                        }
                    }
                    activity?.addFragmentToBackstack(
                        android.R.id.content,
                        fragment
                    )
                }

            })

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    private fun registerNewUser(
        userName: String,
        email: String,
        pass: String,
        phone: String
    ) {
        LoadingDialog.show(requireContext())
        val userId = Utils.shared.autoGenerateId()
        dbReference = FirebaseDatabase.getInstance().getReference(TableKey.USERS.key).child(userId)

        val hasMap: HashMap<String, String> = HashMap()
        hasMap[UserKey.USERID.key] = userId
        hasMap[UserKey.USERNAME.key] = userName
        hasMap[UserKey.EMAIL.key] = email
        hasMap[UserKey.PASSWORD.key] = pass
        hasMap[UserKey.AVATAR.key] = ""
        hasMap[UserKey.LOCATION.key] = ""
        hasMap[UserKey.IS_SHARE_LOCATION.key] = "0"
        hasMap[UserKey.IS_ONLINE.key] = "0"
        hasMap[UserKey.UPDATED_LOCATION.key] = ""
        hasMap[UserKey.ADDRESS.key] = ""
        hasMap[UserKey.PHONE_NUMBER.key] = phone
        Log.d(TAG, "onVerificationCompleted:2 ")
        dbReference.setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //open login activity
                LoadingDialog.dismiss()
                //Log.d(TAG, "onVerificationCompleted:3 ")
                showMessage("Sign up successfully!", "", false, object : NotifyDialog.OnDialogListener {
                    override fun onClickButton(isOk: Boolean) {
                        activity?.supportFragmentManager?.popBackStack()
                    }
                })
            }
        }
    }

    private fun setText() {
        with(rootView) {
            edtEmail.setLabel("Email")
            edtEmail.setHint("Input email (required)")
            edtName.setLabel("Username")
            edtName.setHint("Input username (required)")
            edtPhone.setLabel("Phone number")
            edtPhone.setHint("Input phone number (required)")
            edtPass.setLabel("Password")
            edtPass.setHint("Input password (required)")
            edtConfirmPass.setLabel("Confirm password")
            edtConfirmPass.setHint("Confirm password (required)")
        }
    }
}