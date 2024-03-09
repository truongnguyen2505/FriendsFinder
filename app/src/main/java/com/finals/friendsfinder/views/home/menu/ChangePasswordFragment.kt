package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.customizes.LoadingDialog
import com.finals.friendsfinder.databinding.FragmentChangePasswordBinding
import com.finals.friendsfinder.utilities.UserDefaults
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.utilities.commons.Constants
import com.finals.friendsfinder.views.friends.data.UserInfo
import com.finals.friendsfinder.views.home.MenuFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    companion object {
        fun newInstance(): ChangePasswordFragment {
            val arg = Bundle()
            return ChangePasswordFragment().apply {
                arguments = arg
            }
        }
    }



    private var userInfo: UserInfo? = null
    private lateinit var auth: FirebaseAuth
    override fun bindData() {
        super.bindData()
        setDB()
        setText()
        setListener()
    }

    private fun setDB() {
        auth = Firebase.auth
        userInfo = Utils.shared.getUser()
    }

    private fun setListener() {
        with(rootView) {
            edtOldPass.setTypePassWord()
            edtOldPass.showImagePassword(true)
            edtNewPass.setTypePassWord()
            edtNewPass.showImagePassword(true)
            edtNewConfirmPass.setTypePassWord()
            edtNewConfirmPass.showImagePassword(true)
            btnChange.clickWithDebounce {
                changePass()
            }
            layoutHeader.imgBack.clickWithDebounce{
                activity?.supportFragmentManager?.popBackStack()
            }
            layoutHeader.tvMessage.text = "Change Password"
        }
    }

    private fun changePass() {
        with(rootView) {
            val oldPass = edtOldPass.getText()
            val newPass = edtNewPass.getText()
            val cfPass = edtNewConfirmPass.getText()
            if (oldPass.isEmpty()) {
                edtOldPass.setMessageError("Password is not empty!")
                return
            }
            if (newPass.length < 8) {
                edtOldPass.setMessageError("Password has to contain from 8 characters!")
                return
            }
            if (!userInfo?.password.equals(oldPass, true)) {
                edtOldPass.setMessageError("It's not current password!")
                return
            }
            if (newPass.isEmpty()) {
                edtNewPass.setMessageError("Password is not empty!")
                return
            }
            if (newPass.length < 8) {
                edtNewPass.setMessageError("Password has to contain from 8 characters!")
                return
            }
            if (cfPass.isEmpty()) {
                edtNewConfirmPass.setMessageError("Confirm is not empty!")
                return
            }
            if (!newPass.equals(cfPass, true)) {
                edtNewConfirmPass.setMessageError("Password is not match!")
                return
            }
            LoadingDialog.show(requireContext())
            auth.currentUser?.updatePassword(newPass)
                ?.addOnCompleteListener { task ->
                    LoadingDialog.dismiss()
                    if (task.isSuccessful) {
                        showMessage("Updated successfully!")
                        userInfo?.password = newPass
                        userInfo?.userId?.let {
                            FirebaseDatabase.getInstance().getReference("Users").child(it)
                                .setValue(userInfo).addOnCompleteListener {
                                activity?.supportFragmentManager?.popBackStack()
                            }
                        }
                        edtOldPass.setText("")
                        edtOldPass.hideMessageError()
                        edtNewPass.setText("")
                        edtNewPass.hideMessageError()
                        edtNewConfirmPass.setText("")
                        edtNewConfirmPass.hideMessageError()
                    } else {
                        showMessage(getString(R.string.str_error_occurs))
                    }
                }
        }
    }

    private fun getUserInfo() {
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (dataSnap: DataSnapshot in snapshot.children) {
                    val user = dataSnap.getValue(UserInfo::class.java)
                    //check not me
                    if (user?.userId.equals(auth.currentUser?.uid)) {
                        val gson = Gson()
                        val json = gson.toJson(user)
                        UserDefaults.standard.setSharedPreference(Constants.CURRENT_USER, json)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setText() {
        with(rootView) {
            edtOldPass.setLabel("Old password")
            edtOldPass.setHint("Input old password")
            edtNewPass.setLabel("New password")
            edtNewPass.setHint("Input new password")
            edtNewConfirmPass.setLabel("Confirm new password")
            edtNewConfirmPass.setHint("Input confirm new password")
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(inflater)
    }
}