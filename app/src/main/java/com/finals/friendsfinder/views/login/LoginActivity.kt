package com.finals.friendsfinder.views.login

import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityLoginBinding
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var auth: FirebaseAuth
    private var fbUser: FirebaseUser? = null
    override fun observeHandle() {
        super.observeHandle()
    }

    override fun setupView() {
        super.setupView()
        setupDB()
        checkCurrentUser()
        setText()
        setListener()
    }

    private fun checkCurrentUser() {
        if (fbUser != null){
            showActivity<MainActivity>(goRoot = true)
        }
    }

    private fun setText() {
        with(rootView){
            edtEmail.setLabel("Email")
            edtPass.setLabel("Password")
        }
    }

    private fun setListener() {
        with(rootView) {
            edtPass.setTypePassWord()
            btnLogin.clickWithDebounce {
                loginNow()
            }
        }
    }

    private fun loginNow() {
        with(rootView) {
            val email = edtEmail.getText()
            val pass = edtPass.getText()
            if (email.isEmpty() && pass.isEmpty()) {
                edtEmail.setMessageError("Email can not be empty!")
            } else if (email.isEmpty() && pass.isNotEmpty()) {
                edtEmail.setMessageError("Email can not be empty!")
            } else if (email.isNotEmpty() && pass.isEmpty()) {
                edtPass.setMessageError("Password can not be empty!")
            } else {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this@LoginActivity) {
                        if (it.isSuccessful){
                            showActivity<MainActivity>(goRoot = true)
                        }else{
                            showMessage("Email or password invalid!")
                        }
                    }
            }
        }

    }

    private fun setupDB() {
        auth = com.google.firebase.ktx.Firebase.auth
        fbUser = com.google.firebase.ktx.Firebase.auth.currentUser!!
    }

    override fun setupEventControl() {
        super.setupEventControl()
        with(rootView) {
            btnSignup.clickWithDebounce {
                showActivity<SignUpFragment>(goRoot = false)
            }
        }
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
}