package com.finals.friendsfinder.views.login

import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.databinding.FragmentSignUpBinding
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.SignupKey
import com.finals.friendsfinder.utilities.showActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment: BaseActivity<FragmentSignUpBinding>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference

    override fun observeHandle() {
        super.observeHandle()
    }

    override fun setupView() {
        super.setupView()
        setupDB()
        setText()
        setListener()
    }

    private fun setListener() {
        with(rootView){
            edtPass.setTypePassWord()
            edtConfirmPass.setTypePassWord()
            edtPass.setOnFocusChangeListener { view, b ->
                if (!b)
                    edtPass.hideMessageError()
            }
            edtPass.onTextChangeCallback = {txt ->
                if(txt.length < 8)
                    edtPass.setMessageError("Password must be greater than or equal to 8 characters!")
                else edtPass.hideMessageError()
            }
            btnSignUp.clickWithDebounce {
                checkEmptyToRegister()
            }
        }
    }

    private fun checkEmptyToRegister() {
        with(rootView){
            val userName = edtName.getText()
            val email = edtEmail.getText()
            val pass = edtPass.getText()
            val cfPass = edtConfirmPass.getText()
            if (userName.isEmpty()){
                edtName.setMessageError("Username is not empty!")
                return
            }
            if (email.isEmpty()){
                edtEmail.setMessageError("Email is not empty!")
                return
            }
            if (pass.isEmpty()){
                edtPass.setMessageError("Password is not empty!")
                return
            }
            if (pass.length < 8){
                return
            }
            if (cfPass.isEmpty()){
                edtConfirmPass.setMessageError("Confirm is not empty!")
                return
            }
            if (!pass.equals(cfPass, true)){
                edtConfirmPass.setMessageError("Password is not match!")
                return
            }
            registerNewUser(userName, email, pass)
        }

    }

    private fun registerNewUser(userName: String, email:String, pass: String){
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this@SignUpFragment){ it ->
                if (it.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user?.uid ?: ""
                    dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hasMap: HashMap<String, String> = HashMap()
                    hasMap[SignupKey.USERID.key] = userId
                    hasMap[SignupKey.USERNAME.key] = userName
                    hasMap[SignupKey.PROFILE_IMAGE.key] = ""

                    dbReference.setValue(hasMap).addOnCompleteListener(this){task ->
                        if (task.isSuccessful){
                            //open login activity
                            showActivity<LoginActivity>(goRoot = true)
                        }
                    }

                }else if (it.isComplete){
                    showMessage("Email has registered!")
                }
            }
    }

    private fun setupDB() {
        auth = com.google.firebase.ktx.Firebase.auth
    }

    private fun setText() {
        with(rootView){
            edtEmail.setLabel("Email")
            edtName.setLabel("Username")
            edtPass.setLabel("Password")
            edtConfirmPass.setLabel("Confirm password")
        }
    }

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun getViewBinding(): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(layoutInflater)
    }
}