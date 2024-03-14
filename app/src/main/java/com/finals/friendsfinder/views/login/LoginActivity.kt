package com.finals.friendsfinder.views.login

import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.customizes.LoadingDialog
import com.finals.friendsfinder.databinding.ActivityLoginBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.home.MainActivity
import com.finals.friendsfinder.views.home.menu.PrivacyPolicyFragment
import com.finals.friendsfinder.views.home.menu.TermsOfUsesFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Method

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private var listUser: MutableList<UserInfo> = mutableListOf()
    private var isAccept = false

    override fun observeHandle() {
        super.observeHandle()
        FirebaseDatabase.getInstance().getReference(TableKey.USERS.key)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listUser.clear()
                    for (dataSnap: DataSnapshot in snapshot.children) {
                        val user = dataSnap.getValue(UserInfo::class.java)
                        user?.let { listUser.add(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    listUser.clear()
                }

            })
    }

    override fun setupView() {
        super.setupView()
        checkCurrentUser()
        setText()
        setListener()
    }

    private fun checkCurrentUser() {
        if (BaseAccessToken.token.isNotEmpty()) {
            showActivity<MainActivity>(goRoot = true)
        }
    }

    private fun setText() {
        with(rootView) {
            edtPhone.setLabel("Phone number")
            edtPhone.setHint("Input phone number (required)")
            edtPass.setLabel("Password")
            edtPass.setHint("Input password (required)")
            val userTerm = "Terms of Uses"
            val txtPolicy = "Privacy Policy"
            val completeString = getString(R.string.txt_accept_policy).format(userTerm, txtPolicy)
            spannableText(completeString, userTerm, txtPolicy)
        }
    }

    private fun spannableText(
        completeString: String,
        str1: String,
        str2: String,
    ) {
        val builderText = SpannableStringBuilder(completeString)
        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.color_btn_blue))
        val startIndex: Int = completeString.indexOf(str1)
        val endIndex: Int = startIndex + str1.length
        val startIndex2 = completeString.indexOf(str2)
        val endIndex2 = startIndex2 + str2.length

        val clickUserTerm: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.color_btn_blue)
                val method: Method = TextPaint::class.java.getMethod(
                    "setUnderlineText", Integer.TYPE, java.lang.Float.TYPE
                )
                method.invoke(
                    ds,
                    ContextCompat.getColor(this@LoginActivity, R.color.color_btn_blue),
                    1.0f
                )
            }

            override fun onClick(p0: View) {
                addFragmentToBackstack(
                    android.R.id.content,
                    PrivacyPolicyFragment.newInstance()
                )
            }
        }
        builderText.setSpan(clickUserTerm, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickPolicy: ClickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(this@LoginActivity, R.color.color_btn_blue)
                val method: Method = TextPaint::class.java.getMethod(
                    "setUnderlineText", Integer.TYPE, java.lang.Float.TYPE
                )
                method.invoke(
                    ds,
                    ContextCompat.getColor(this@LoginActivity, R.color.color_btn_blue),
                    1.0f
                )
            }

            override fun onClick(p0: View) {
                addFragmentToBackstack(
                    android.R.id.content,
                    TermsOfUsesFragment.newInstance()
                )
            }
        }
        builderText.setSpan(clickPolicy, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        rootView.tvAgreePolicy.movementMethod = LinkMovementMethod.getInstance()
        rootView.tvAgreePolicy.text = builderText
    }

    private fun setListener() {
        with(rootView) {
            edtPass.setTypePassWord()
            edtPass.showImagePassword(true)
            setEnableBtnLogin(false)
            btnLogin.clickWithDebounce {
                checkSelectedPolicy {
                    loginNow()
                }
            }
            edtPhone.onTextChangeCallback = {
                val txtPass = edtPass.getText()
                if (it.isNotEmpty() && txtPass.isNotEmpty()){
                    setEnableBtnLogin(true)
                }else{
                    setEnableBtnLogin(false)
                }
            }
            edtPass.onTextChangeCallback = {
                val txtPhone = edtPhone.getText()
                if (it.isNotEmpty() && txtPhone.isNotEmpty()){
                    setEnableBtnLogin(true)
                }else{
                    setEnableBtnLogin(false)
                }
            }
            cbAgreePolicy.setOnCheckedChangeListener { compoundButton, b ->
                isAccept = b
                if (isAccept)
                    rootView.layoutSelectAgree.background = null
            }
        }
    }

    private fun checkSelectedPolicy(onClick: (() -> Unit)) {
        if (isAccept) {
            onClick.invoke()
        } else {
            rootView.layoutSelectAgree.setBackgroundResource(R.drawable.bg_check_select_policy)
        }
    }


    private fun setEnableBtnLogin(isEnable: Boolean){
        rootView.btnLogin.isEnabled = isEnable
        if (isEnable)
            rootView.btnLogin.alpha = 1f
        else rootView.btnLogin.alpha = 0.5f
    }

    private fun loginNow() {
        with(rootView) {
            val phone = edtPhone.getText()
            val pass = edtPass.getText()
            if (phone.isEmpty() && pass.isEmpty()) {
                edtPhone.setMessageError("Phone number must not be empty!")
                edtPass.setMessageError("Password must not be empty!")
            } else if (phone.isEmpty() && pass.isNotEmpty()) {
                edtPhone.setMessageError("Phone number must not be empty!")
            } else if (phone.isNotEmpty() && pass.isEmpty()) {
                edtPass.setMessageError("Password must not be empty!")
            } else {
                LoadingDialog.show(this@LoginActivity)
                val newList = listUser.filter {
                    it.phoneNumber == phone
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    LoadingDialog.dismiss()
                    if (newList.isEmpty()){
                        showMessage("Not exist this account!\n Please, try again!")
                    }else{
                        if (newList[0].phoneNumber.equals(phone, true) && newList[0].password.equals(pass, true)){
                            BaseAccessToken.accessToken = newList[0].userId
                            showActivity<MainActivity>(goRoot = true)
                        }else if (newList[0].phoneNumber.equals(phone, true) && !newList[0].password.equals(pass, true)){
                            showMessage("Phone number or password is wrong!\n Please, try again!")
                        }else{
                            showMessage("Not exist this account!\n Please, try again!")
                        }
                    }
                }, 1500L)
            }
        }

    }

    override fun setupEventControl() {
        super.setupEventControl()
        with(rootView) {
            btnSignup.clickWithDebounce {
                addFragmentToBackstack(android.R.id.content, SignUpFragment.newInstance())
            }
        }
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}