package com.finals.friendsfinder.views.login

import android.util.Log
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseActivity
import com.finals.friendsfinder.customizes.LoadingDialog
import com.finals.friendsfinder.databinding.ActivityLoginBinding
import com.finals.friendsfinder.models.BaseAccessToken
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.addFragmentToBackstack
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.utilities.commons.TableKey
import com.finals.friendsfinder.utilities.showActivity
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.home.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private var listUser: MutableList<UserInfo> = mutableListOf()

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
                //LoadingDialog.show(this@LoginActivity)
                val newList = listUser.filter {
                    it.phoneNumber == phone
                }
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