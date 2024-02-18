package com.finals.friendsfinder.views.login

import android.media.MediaPlayer.OnCompletionListener
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.ActivityLoginBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private lateinit var auth: FirebaseAuth
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
        if (BaseAccessToken.token.isNotEmpty()) {
            showActivity<MainActivity>(goRoot = true)
        }
    }

    private fun setText() {
        with(rootView) {
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
                        if (it.isSuccessful) {
                            val fbUser = Firebase.auth.currentUser
                            fbUser?.getIdToken(false)?.addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    val token = task.result.token ?: ""
                                    BaseAccessToken.accessToken = token
                                    if (token.isNotEmpty())
                                        showActivity<MainActivity>(goRoot = true)
                                    else return@addOnCompleteListener
                                }else {
                                    return@addOnCompleteListener
                                }
                            }
                        } else {
                            showMessage("Email or password invalid!")
                        }
                    }
            }
        }

    }

    private fun setupDB() {
        auth = Firebase.auth
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