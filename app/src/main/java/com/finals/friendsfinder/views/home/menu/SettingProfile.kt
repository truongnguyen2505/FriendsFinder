package com.finals.friendsfinder.views.home.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.finals.friendsfinder.R
import com.finals.friendsfinder.bases.BaseFragment
import com.finals.friendsfinder.customizes.LoadingDialog
import com.finals.friendsfinder.databinding.FragmentEditProfileBinding
import com.finals.friendsfinder.utilities.Utils
import com.finals.friendsfinder.utilities.clickWithDebounce
import com.finals.friendsfinder.views.home.MenuFragment
import com.google.firebase.database.FirebaseDatabase

class SettingProfile : BaseFragment<FragmentEditProfileBinding>() {
    companion object {
        fun newInstance(): SettingProfile {
            val arg = Bundle()
            return SettingProfile().apply {
                arguments = arg
            }
        }
    }

    private var isLocationSharing = false

    override fun setupEventControl() {
        super.setupEventControl()
    }

    override fun bindData() {
        super.bindData()
        setText()
        setListener()
        setInfo()
    }

    private fun setInfo() {
        with(rootView) {
            val userInfo = Utils.shared.getUser()
            if (userInfo?.avatar.isNullOrEmpty())
                imgAvatar.setImageResource(R.drawable.ic_avatar_empty_25)
            else Glide.with(requireContext()).load(userInfo?.avatar).into(imgAvatar)
        }
    }

    private fun setText() {
        with(rootView) {
            layoutHeader.tvMessage.text = "Setting Profile"
            edtName.setLabel("Username")
            edtName.setHint("Input username")
            edtAddress.setLabel("Address")
            edtAddress.setHint("Input address")
        }
    }

    private fun updateUserInfo() {
        LoadingDialog.show(requireContext())
        val userInfo = Utils.shared.getUser()
        with(rootView) {
            val nameUser = edtName.getText()
            val add = edtAddress.getText()
            if (nameUser.isEmpty()) {
                edtName.setMessageError("Username is not empty!")
                return
            }
            userInfo?.userName = nameUser
            userInfo?.address = add
            userInfo?.shareLocation = if (isLocationSharing) "1" else "0"
            FirebaseDatabase.getInstance().getReference("Users").child(userInfo?.userId ?: "")
                .setValue(userInfo).addOnCompleteListener {
                    LoadingDialog.dismiss()
                    if (it.isSuccessful){
                        showMessage("Update profile successfully!")
                    }else{
                        showMessage(getString(R.string.str_error_occurs))
                    }
            }
        }
    }

    private fun setListener() {
        with(rootView) {
            layoutHeader.imgBack.clickWithDebounce {
                activity?.supportFragmentManager?.popBackStack()
            }
            imgSelectorLocation.clickWithDebounce {
                imgSelectorLocation.isSelected = !imgSelectorLocation.isSelected
                isLocationSharing = imgSelectorLocation.isSelected
                Log.d("TAG", "isLocationSharing: $isLocationSharing")
            }
            btnSave.clickWithDebounce {
                updateUserInfo()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentEditProfileBinding {
        return FragmentEditProfileBinding.inflate(inflater)
    }
}